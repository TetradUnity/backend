package com.tetradunity.server.models.subjects;

public enum TypeSubject {
    ANNOUNCED_SUBJECT(0),
    PREPARING_SUBJECT(1),
    READY_SUBJECT(2),
    ACTIVE_SUBJECT(3);

    int type;

    TypeSubject(int type){
        this.type = type;
    }

    int getType(){
        return type;
    }

    static public TypeSubject getTypeSubject(int type){
        for(TypeSubject temp : TypeSubject.values()){
            if(temp.getType() == type){
                return temp;
            }
        }
        return null;
    }
}