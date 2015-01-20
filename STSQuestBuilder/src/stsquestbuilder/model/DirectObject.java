/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 * Essentially an exact copy of the Direct object class in the unity project
 * 
 * @author William
 */
public class DirectObject {
    private String name;
    private String type;

    public DirectObject(String id, String typeId) {
        name = id;
        type = typeId;
    }
    
    /**
     * This constructor provides for simple conversion from a direct object protobuf
     * to a direct object
     * @param directObject object to convert 
     */
    public DirectObject(QuestProtobuf.DirectObjectProtocol directObject) {
        name = directObject.getName();
        type = directObject.getType();
    }

    /**
     * Returns a string identifier corresponding to this particular
     * direct object.  Used in serialization
     */
    public String getIdentifier() {
            return name;
    }

    /**
     * Returns a string identifier corresponding to the type of this
     * direct object.  Used in serialization
     */
    public String getTypeIdentifier() {
            return type;
    }
    
    /**
     * Creates a direct object protobuf and returns it
     * @return a protobuf object containing the information from this object
     */
    public QuestProtobuf.DirectObjectProtocol getDirectObjectAsProtobuf() {
        QuestProtobuf.DirectObjectProtocol.Builder builder = QuestProtobuf.DirectObjectProtocol.newBuilder();
        builder.setName(name);
        builder.setType(type);
        return builder.build();
    }
    
    /*
     * Object overrides
     */
    
    @Override
    public int hashCode() {
        return (name + type).hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof DirectObject) {
            DirectObject o = (DirectObject)other;
            return o.name.equals(name) && o.type.equals(type);
        }
        return false;
    }
}
