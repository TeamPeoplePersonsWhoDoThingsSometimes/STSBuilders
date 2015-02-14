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
        NPC,
        CONVERSATION_NODE
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
            case CONVERSATION_NODE_HIT:
                return ObjectType.CONVERSATION_NODE;
            default:
                return ObjectType.ENEMY;
        }
    }
    
    /**
     * NOTE: ConversationNodes returned by this method are in BAD PRACTICE for
     * Conversation Node definition, they do not have a root conversation
     * 
     * @param type
     * @return 
     */
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
            case CONVERSATION_NODE:
                directObject = new ConversationNode();
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
            case CONVERSATION_NODE:
                try {
                    directObject = ConversationNode.getNodeByID(Integer.parseInt(proto.getType()));
                } catch (NumberFormatException excep) {
                    System.err.println("Malformed Conversation Node Protobuf- " + proto.getType());
                    System.err.println("Building node as naked direct object instead");
                    directObject = new DirectObject(proto);
                }
                break;
            default:
                directObject = new DirectObject(proto);
                break;
        }
        
        return directObject;
    }
    
}
