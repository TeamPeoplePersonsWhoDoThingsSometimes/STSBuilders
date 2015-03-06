package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf;
import stsquestbuilder.protocolbuffers.QuestProtobuf.LevelSpecification;

/**
 *
 * DirectObject format:
 * id- type
 * typeId- weapon name
 * 
 * @author William
 */
public class Item extends DirectObject {
    
    private ItemType type;
    private String name;
    private LevelSpecification levelSpec;
    private int version;

    public Item() {
        super("","");
        type = ItemType.types.get(0);
        name = "";
        version = 0;
        levelSpec = LevelSpecification.MINIMUM;
    }
    
    public Item(String n, int v, ItemType type) {
        super(type.getName(), n);
        this.type = type;
        name = n;
        version = v;
        levelSpec = LevelSpecification.MINIMUM;
    }
    
    public Item(QuestProtobuf.DirectObjectProtocol DirObj) {
        super(DirObj);
        type = ItemType.parse(DirObj.getName());
        name = DirObj.getType();
        version = DirObj.getItemInformation().getVersion();
        levelSpec = DirObj.getItemInformation().getLevelSpec();
    }

    public LevelSpecification getLevelSpec() {
        return levelSpec;
    }

    public void setLevelSpec(LevelSpecification levelSpec) {
        this.levelSpec = levelSpec;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        super.setTypeId(name);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
        super.setIdentifier(type.getName());
    }
    
    /**
     * Overrides direct object method in order to specify item information
     * protocol within protobuf
     * @return 
     */
    @Override
    public QuestProtobuf.DirectObjectProtocol getDirectObjectAsProtobuf() {
        QuestProtobuf.DirectObjectProtocol.Builder builder = QuestProtobuf.DirectObjectProtocol.newBuilder();
        QuestProtobuf.ItemProtocol.Builder itemBuilder = QuestProtobuf.ItemProtocol.newBuilder();
        builder.setName(type.toString());
        builder.setType("" + name);
        builder.setAmount(super.getAmount());
        itemBuilder.setLevelSpec(levelSpec);
        itemBuilder.setVersion(version);
        builder.setItemInformation(itemBuilder);
        return builder.build();
    }
    
}
