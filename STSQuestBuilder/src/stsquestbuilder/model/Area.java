package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf.DirectObjectProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.MapProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.MapType;

import java.util.HashMap;

/**
 * Note to developers... areas are obnoxious... if you have a better way to propogate the ids of areas globally
 * please fix this, but until then, whenever we loose a reference to an area, we need to remove the ids from
 * the static lists first via deleteId()
 * 
 * Anyway, this class represtents an area description that corresponds to an area that might be near to the player within a given range
 * 
 * @author William
 */
public class Area extends DirectObject {
    
    /*public static Area getAreaById(long id) {
        return areaIdMap.get(id);
    }
    
    public static boolean idExists(long id) {
        return areaIdMap.containsKey(id);
    }
    
    public static boolean uiIdExists(String id) {
        return uiToIdMap.containsKey(id);
    }
    
    public static long getNextUID() {
        return nextUid++;
    }
    
    private static HashMap<String, Long> uiToIdMap = new HashMap<>();//ui id is the frontend, human friendly identifier for the designer
    private static HashMap<Long, Area> areaIdMap = new HashMap<>();
    private static long nextUid = 0;*/
    
    private MapType type;
    //private long uid;
    private int x;
    private int y;

    public Area() {
        super("Area", "");
        //uid = getNextUID();
        x = 0;
        y = 0;
    }
    
    public Area(DirectObjectProtocol proto) {
        super(proto.getName(), proto.getType());
        //uid = proto.getMap().getUid();
        //type = MapType.valueOf(proto.getType());
        String[] p = proto.getType().split(" ");
        
        if (p.length == 2) {
            x = Integer.valueOf(p[0]);
            y = Integer.valueOf(p[1]);
        } else {
            x = 0;
            y = 0;
        }
    }
    
    /**
     * Neccesary to ensure that deleted areas ids are not kept
     */
    /*public void deleteId() {
        uiToIdMap.remove(name);
        areaIdMap.remove(uid);
    }*/
    
    public MapType getType() {
        return type;
    }

    public void setType(MapType type) {
        this.type = type;
        super.setTypeId(type.toString());
    }

    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        if(uiToIdMap.containsKey(this.name)) {
            uiToIdMap.remove(this.name);
        }
        this.name = name;
        uiToIdMap.put(name, uid);
        super.setIdentifier(name);
    }

    public boolean isGenerateIfNeeded() {
        return generateIfNeeded;
    }

    public void setGenerateIfNeeded(boolean generateIfNeeded) {
        this.generateIfNeeded = generateIfNeeded;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }*/
    
    public int getY() {
        return y;
    }
    
    public int getX() {
        return x;
    }
    
    public void setY(int newY) {
        y = newY;
        fixType();
    }
    
    public void setX(int newX) {
        x = newX;
        fixType();
    }
    
    /**
     * updates the direct object's type with the area information
     */
    private void fixType() {
        super.setTypeId("" + x + " " + y);
    }
    
    public DirectObjectProtocol getDirectObjectAsProtobuf() {
        DirectObjectProtocol.Builder builder = DirectObjectProtocol.newBuilder();
        builder.setName(super.getIdentifier());
        builder.setType(super.getTypeIdentifier());
        
        return builder.build();
    }
    
}
