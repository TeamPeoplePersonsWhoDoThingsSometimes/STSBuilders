package stsquestbuilder.model;

import stsquestbuilder.model.DirectObject;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

import stsquestbuilder.protocolbuffers.QuestProtobuf.SpawnCommandProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.MapType;

/**
 * Class has many optional values as opposed to using polymorphism in order to easily
 * match protobuf optional fields and lack of simple polymorphism
 * @author William
 */
public class SpawnCommand {
    
    private MapType spawnArea;
    private QuestProtobuf.SpawnAreaTypeSpecification specification;
    private int range;
    private int quantity;
    
    //following fields are singularly exclusive
    //private Area areaToSpawn;
    private Item itemToSpawn;
    private Enemy enemyToSpawn;

    //preserve default
    public SpawnCommand() {
        spawnArea = MapType.CITY;
        range = 1;
        quantity = 1;
        specification = QuestProtobuf.SpawnAreaTypeSpecification.LOCAL;
    }
    
    public SpawnCommand(SpawnCommandProtocol proto) {
        spawnArea = proto.getSpawnArea();
        range = proto.getRange();
        specification = proto.getSpawnSpecification();
        
        /*if (proto.hasArea()) {
            areaToSpawn = new Area(proto.getArea());
        }*/
        
        if (proto.hasItem()) {
            itemToSpawn = new Item(proto.getItem());
        }
        
        if(proto.hasEnemy()) {
            enemyToSpawn = new Enemy(proto.getEnemy());
        }
    }
    
    public MapType getSpawnArea() {
        return spawnArea;
    }

    /**
     * Sets the area in which to spawn
     * @param spawnArea 
     */
    public void setSpawnArea(MapType spawnArea) {
        this.spawnArea = spawnArea;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /*public Area getAreaToSpawn() {
        return areaToSpawn;
    }*/

    /**
     * Sets the area which will be spawned
     * @param areaToSpawn 
     */
    /*public void setAreaToSpawn(Area areaToSpawn) {
        this.areaToSpawn = areaToSpawn;
        itemToSpawn = null;
        enemyToSpawn = null;
    }*/

    public Item getItemToSpawn() {
        return itemToSpawn;
    }

    public void setItemToSpawn(Item itemToSpawn) {
        this.itemToSpawn = itemToSpawn;
        //areaToSpawn = null;
        enemyToSpawn = null;
    }

    public Enemy getEnemyToSpawn() {
        return enemyToSpawn;
    }

    public void setEnemyToSpawn(Enemy enemyToSpawn) {
        this.enemyToSpawn = enemyToSpawn;
        //areaToSpawn = null;
        itemToSpawn = null;
    }
    
    @Override
    public String toString() {
        DirectObjectFactory.ObjectType type = commandType();
        /*if(type.equals(DirectObject.ObjectType.AREA)) {
            return "Area Spawn";
        } else*/ if(type.equals(DirectObjectFactory.ObjectType.ENEMY)) {
            return "Enemy Spawn";
        } else if(type.equals(DirectObjectFactory.ObjectType.ITEM)) {
            return "Item Spawn";
        }
        return "Empty Command";
    }
    
    /**
     * Gets this command's type,
     * @return the command's type int 
     */
    public DirectObjectFactory.ObjectType commandType() {
        /*if(areaToSpawn != null) {
            return DirectObject.ObjectType.AREA;
        } else*/ if(enemyToSpawn != null) {
            return DirectObjectFactory.ObjectType.ENEMY;
        } else if(itemToSpawn != null) {
            return DirectObjectFactory.ObjectType.ITEM;
        }
        return DirectObjectFactory.ObjectType.EMPTY;
    }

    public QuestProtobuf.SpawnAreaTypeSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(QuestProtobuf.SpawnAreaTypeSpecification specification) {
        this.specification = specification;
    }
    
    public SpawnCommandProtocol getSpawnCommandAsProto() {
        SpawnCommandProtocol.Builder builder = SpawnCommandProtocol.newBuilder();
        
        builder.setRange(range);
        builder.setSpawnSpecification(specification);
        /*if (commandType().equals(DirectObject.ObjectType.AREA))
            builder.setArea(areaToSpawn.getDirectObjectAsProtobuf());
        else*/
        if (commandType().equals(DirectObjectFactory.ObjectType.ENEMY))
            builder.setEnemy(enemyToSpawn.getDirectObjectAsProtobuf());
        else if (commandType().equals(DirectObjectFactory.ObjectType.ITEM))
            builder.setItem(itemToSpawn.getDirectObjectAsProtobuf());
        
        builder.setSpawnArea(spawnArea);
        return builder.build();
    }
    
}
