package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf.DirectObjectProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.MapProtocol;

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
    
    public enum MapType {
        CITY,
        PATH,
        DUNGEON;
    }
    
    public static Area getAreaById(long id) {
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
    private static long nextUid = 0;
    
    private MapType type;
    private long uid;
    private String name;
    private boolean generateIfNeeded;
    private double range;

    public Area() {
        super("", "");
        uid = getNextUID();
        name = "";//no frontend id yet
        generateIfNeeded = false;
    }
    
    public Area(DirectObjectProtocol proto) {
        super(proto.getName(), proto.getType());
        uid = proto.getMap().getUid();
        name = proto.getName();
        type = MapType.valueOf(proto.getType());
        range = proto.getMap().getRange();
        generateIfNeeded = proto.getMap().getGenerateIfNeeded();
    }
    
    /**
     * Neccesary to ensure that deleted areas ids are not kept
     */
    public void deleteId() {
        uiToIdMap.remove(name);
        areaIdMap.remove(uid);
    }
    
    public MapType getType() {
        return type;
    }

    public void setType(MapType type) {
        this.type = type;
        super.setTypeId(type.toString());
    }

    public String getName() {
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
    }
    
    public DirectObjectProtocol getDirectObjectAsProtobuf() {
        DirectObjectProtocol.Builder builder = DirectObjectProtocol.newBuilder();
        builder.setName(name);
        builder.setType(type.toString());
        
        MapProtocol.Builder mapBuilder = MapProtocol.newBuilder();
        mapBuilder.setGenerateIfNeeded(generateIfNeeded);
        mapBuilder.setRange(range);
        mapBuilder.setUid(uid);
        
        builder.setMap(mapBuilder.build());
        
        return builder.build();
    }
    
}
