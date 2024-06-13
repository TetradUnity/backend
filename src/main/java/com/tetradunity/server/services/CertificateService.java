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
import java.net.URISyntaxException;
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

    private final static double pageWidth = PDRectangle.A4.getHeight();

    private final File certificateTemplate;
    private final File fontItalicFile;
    private final File fontNameFile;
    private final File fontTextFile;
    private final File certificateFile;

    {
        try {
            certificateTemplate = new File(getClass().getClassLoader().getResource("templates\\CertificateTemplate.jpg").toURI());
            fontItalicFile = new File(getClass().getClassLoader().getResource("templates\\NotoSansItalic.ttf").toURI());
            fontNameFile = new File(getClass().getClassLoader().getResource("templates\\MTCORSVA.TTF").toURI());
            fontTextFile = new File(getClass().getClassLoader().getResource("templates\\CENTURY.TTF").toURI());
            certificateFile = new File(getClass().getClassLoader().getResource("templates\\certificate.pdf").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void presentCertificates(long subject_id) throws IOException {
        SubjectEntity subject = subjectRepository.findById(subject_id).orElse(null);

        if (!subject.educationProcess()) {
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
        UserEntity student;
        for (long student_id : students_id) {
            grades = gradeRepository.getGradesForStudentAndSubject(student_id, subject_id);
            result = 0D;
            for (double grade : grades) {
                result += grade;
            }
            try {
                result /= grades.size();
            } catch (ArithmeticException ae) {
                continue;
            }
            student = userRepository.findById(student_id).orElse(null);
            if (student == null) {
                continue;
            }
            certificate = new CertificateEntity(student_id, subject_id, result);
            document = new PDDocument();
            page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            image = PDImageXObject.createFromFileByContent(certificateTemplate, document);
            contentStream.drawImage(image, 21, 18, 800, 560);
            contentStream.setNonStrokingColor(Color.WHITE);
            PDType0Font font = PDType0Font.load(document, fontNameFile);
            textToCenter(contentStream, student.getFirst_name() + " " + student.getLast_name(), font, 50, 370);
            font = PDType0Font.load(document, fontTextFile);
            textToCenter(contentStream, "Сертифікат за " + certificate.getType().getDescription(), font, 40, 310);
            textToCenter(contentStream, "під час навчання на курсі", font, 40, 270);
            textToCenter(contentStream, "\"" + subject.getTitle() + "\"", font, 40, 230);
            font = PDType0Font.load(document, fontItalicFile);
            contentStream.setFont(font, 15);
            contentStream.beginText();
            String text_date = currentTime.get(Calendar.DAY_OF_MONTH) + "." + currentTime.get(Calendar.MONTH) + "." + currentTime.get(Calendar.YEAR);
            contentStream.newLineAtOffset(310 - (font.getStringWidth(text_date) / 1000 * 15) / 2, 120);
            contentStream.showText(text_date);
            contentStream.endText();
            UUID uid = UUID.randomUUID();
            while (true) {
                if (certificateRepository.certificateExists(uid)) {
                    uid = UUID.randomUUID();
                } else {
                    break;
                }
            }
            certificate.setUid(uid);
            contentStream.beginText();
            contentStream.newLineAtOffset(420, 120);
            contentStream.showText(uid.toString());
            contentStream.endText();
            contentStream.close();
            document.save(certificateFile);
            storageService.uploadCertificate(certificateFile, uid);
            certificateRepository.save(certificate);
        }
    }

    private void textToCenter(PDPageContentStream contentStream, String text, PDType0Font font, int size_font, int y) throws IOException {
        contentStream.setFont(font, size_font);
        contentStream.beginText();
        contentStream.newLineAtOffset((float)(pageWidth - font.getStringWidth(text) / 1000 * size_font) / 2, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}