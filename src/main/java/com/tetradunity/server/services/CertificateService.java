package com.tetradunity.server.services;

import com.tetradunity.server.entities.CertificateEntity;
import com.tetradunity.server.entities.StudentSubjectEntity;
import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.repositories.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;

    private final static File certificateTemplate = new File("C:\\Users\\maksi\\IdeaProjects\\backend\\src\\main\\resources\\templates\\certificateTemplate.jpg");
    private final static File fontItalicFile =  new File("C:\\Users\\maksi\\IdeaProjects\\backend\\src\\main\\resources\\templates\\NotoSansItalic.ttf");
    private final static File fontRobotoFile = new File("C:\\Users\\maksi\\IdeaProjects\\backend\\src\\main\\resources\\templates\\Roboto-Black.ttf");
    private final static File certificateFile = new File("C:\\Users\\maksi\\IdeaProjects\\backend\\src\\main\\resources\\templates\\certificate.pdf");

    public synchronized void presentCertificates(long subject_id) throws IOException {
        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if(!subject.educationProcess()){
            return;
        }

        List<Long> students_id = studentSubjectRepository
                .findBySubject_id(subject_id)
                .stream()
                .map(StudentSubjectEntity::getStudent_id)
                .toList();

        List<Double> grades;
        double result;
        CertificateEntity certificate;
        PDDocument document;
        PDPage page;
        PDPageContentStream contentStream;
        PDImageXObject image;
        Calendar currentTime = new GregorianCalendar();
        UserEntity user;
        for(long student_id : students_id){
            grades = gradeRepository.getGradesForStudentAndSubject(student_id, subject_id);
            result = 0D;
            for(double grade : grades){
                result += grade;
            }
            try{
                result /= grades.size();
            }catch(ArithmeticException ae){
                continue;
            }
            user = userRepository.findById(student_id).orElse(null);
            if(user == null){
                continue;
            }
            certificate = new CertificateEntity(student_id, subject_id, result);
            document = new PDDocument();
            System.out.println(PDRectangle.A4.getHeight());
            System.out.println(PDRectangle.A4.getWidth());
            page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            image = PDImageXObject.createFromFileByContent(certificateTemplate, document);
            contentStream.drawImage(image, 21, 58, 800, 480);
            contentStream.setFont(PDType0Font.load(document, fontRobotoFile), 30);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.beginText();
            contentStream.newLineAtOffset(120, 350);
            contentStream.showText("Сертифікат за " + certificate.getType().getDescription());
            contentStream.endText();
            contentStream.beginText();
            contentStream.newLineAtOffset(120, 320);
            contentStream.showText("під час навчання на курсі");
            contentStream.endText();
            contentStream.beginText();
            contentStream.newLineAtOffset(120, 290);
            contentStream.showText("\"" + subject.getTitle() + "\"");
            contentStream.endText();
            contentStream.beginText();
            contentStream.newLineAtOffset(120, 260);
            contentStream.showText(user.getFirst_name() + " " + user.getLast_name());
            contentStream.endText();
            contentStream.setFont(PDType0Font.load(document, fontItalicFile), 15);
            contentStream.beginText();
            contentStream.newLineAtOffset(710, 75);
            contentStream.showText(currentTime.get(Calendar.DAY_OF_MONTH) + "." + currentTime.get(Calendar.MONTH) + "." + currentTime.get(Calendar.YEAR));
            contentStream.endText();
            UUID uid = UUID.randomUUID();
            while(true){
                if(certificateRepository.certificateExists(uid)){
                    uid = UUID.randomUUID();
                }
                else{
                    break;
                }
            }
            certificate.setUid(uid);
            contentStream.beginText();
            contentStream.newLineAtOffset(550, 30);
            contentStream.showText(uid.toString());
            contentStream.endText();
            contentStream.close();
            document.save(certificateFile);
            storageService.uploadCertificate(certificateFile, uid);
            certificateRepository.save(certificate);
        }
    }
}