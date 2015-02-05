/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.model;

/**
 *
 * @author William
 */
public class Item extends DirectObject {
    private String name;
    private int version;

    public Item() {
        super("","");
    }
    
    public Item(String n, int v) {
        super(n,"" + v);
        name = n;
        version = v;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        super.setIdentifier(name);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
        super.setTypeId("" + version);
    }
    
}
