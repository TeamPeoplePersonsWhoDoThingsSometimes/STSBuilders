package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 *
 * @author William
 */
public class DirectObjectFactory {
    
    public enum ObjectType {
        ITEM,
        AREA,
        ENEMY,
        NPC
    }
    
    public static ObjectType getObjectTypeForActionType(QuestProtobuf.ActionType type) {
        switch(type) {
            case KILL:
            case ATTACK:
                return ObjectType.ENEMY;
            case MOVE_AREA:
                return ObjectType.AREA;
            case EQUIP_ITEM:
            case USE_ITEM:
            case APPROACHED_OBJECT:
            case PICKED_UP_OBJECT:
                return ObjectType.ITEM;
            default:
                return ObjectType.ENEMY;
        }
    }
    
    public static DirectObject buildObjectByType(QuestProtobuf.ActionType type) {
        DirectObject directObject;
        switch(getObjectTypeForActionType(type)) {
            case ENEMY:
                directObject = new Enemy();
                break;
            case AREA:
                directObject = new Area();
                break;
            case ITEM:
                directObject = new Item();
                break;
            default:
                directObject = new Enemy();
                break;
        }
        
        return directObject;
    }
    
    public static DirectObject buildObjectByTypeWithProto(QuestProtobuf.ActionType type, QuestProtobuf.DirectObjectProtocol proto) {
        DirectObject directObject;
        switch(getObjectTypeForActionType(type)) {
            case ENEMY:
                directObject = new Enemy(proto);
                break;
            case AREA:
                directObject = new Area(proto);
                break;
            case ITEM:
                directObject = new Item(proto);
                break;
            default:
                directObject = new DirectObject(proto);
                break;
        }
        
        return directObject;
    }
    
}
