package cz.fit.cvut.tjv.game_store.buisness;

public class EntityStateException extends RuntimeException{
    public EntityStateException (){}

    public <Entity> EntityStateException(Entity entity) {
        super("Illegal state of entity :" + entity);
    }
}
