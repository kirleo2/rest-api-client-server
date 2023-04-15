package cz.fit.cvut.tjv.game_store.buisness;

import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityAlreadyExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.domain.DomainEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public abstract class AbstractCrudService <Entity extends DomainEntity<Key>, Key>{

    protected final CrudRepository<Entity, Key> repository;
    protected AbstractCrudService(CrudRepository <Entity, Key> repository) {
        this.repository = repository;
    }
    public Entity create(Entity entity) throws EntityStateException{
        Key id = entity.getID();
        if (id != null && repository.existsById(id))
            throw new EntityAlreadyExistsException();
        return repository.save(entity);
    }
    public Optional<Entity> readByID(Key id) {
        return repository.findById(id);
    }
    public Iterable<Entity> readAll() {
        return repository.findAll();
    }
    public Entity update(Entity entity) throws EntityStateException{
        if (repository.existsById(entity.getID()))
            return repository.save(entity);
        else
            throw new EntityDoesNotExistsException();
    }
    public void deleteByID(Key id) {
        if (repository.existsById(id))
            repository.deleteById(id);
        else
            throw new EntityDoesNotExistsException();
    }
}
