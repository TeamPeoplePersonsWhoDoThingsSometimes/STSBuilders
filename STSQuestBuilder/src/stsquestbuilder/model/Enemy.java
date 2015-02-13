package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf.DirectObjectProtocol;

/**
 *
 * @author William
 */
public class Enemy extends DirectObject {
    
    private EnemyType generalType;//general enemy type
    private String particularType;//specified type within enemy type
    
    public Enemy() {
        super("","", 0);
        generalType = EnemyType.enemyTypes.get(0);
    }
    
    public Enemy(DirectObjectProtocol proto) {
        super(proto);
        generalType = EnemyType.parse(proto.getName());
        particularType = proto.getType();
    }
    
    public EnemyType getGeneralType() {
        return generalType;
    }
    
    public void setEnemyType(EnemyType type) {
        generalType = type;
        super.setIdentifier(type.toString());
    }
    
    public String getParticularType() {
        return particularType;
    }
    
    public void setParticularType(String type) {
        particularType = type;
        super.setTypeId(type);
    }
    
    @Override
    public String toString() {
        return "" + getGeneralType().getName() + ":" + getParticularType() + ":" + super.getAmount();
    }
    
    @Override
    public DirectObjectProtocol getDirectObjectAsProtobuf() {
        DirectObjectProtocol.Builder builder = DirectObjectProtocol.newBuilder();
        
        builder.setName(generalType.getName());
        builder.setType("" + particularType);
        builder.setAmount(super.getAmount());
        
        return builder.build();
    }
}
