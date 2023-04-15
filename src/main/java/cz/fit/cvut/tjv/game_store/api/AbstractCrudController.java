package cz.fit.cvut.tjv.game_store.api;

import cz.fit.cvut.tjv.game_store.buisness.AbstractCrudService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityAlreadyExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.domain.DomainEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public abstract class AbstractCrudController <Entity extends DomainEntity<Key>, Dto, Key> {
    protected AbstractCrudService<Entity, Key> service;
    protected Function<Entity, Dto> toDtoConverter;
    protected Function<Dto, Entity> toEntityConverter;

    public AbstractCrudController(AbstractCrudService<Entity, Key> service,
                                  Function<Entity, Dto> toDtoConverter,
                                  Function<Dto, Entity> toEntityConverter) {
        this.service = service;
        this.toDtoConverter = toDtoConverter;
        this.toEntityConverter = toEntityConverter;
    }
    @GetMapping
    public Collection<Dto> readAll() {
        return StreamSupport.stream(service.readAll().spliterator(), false).map(toDtoConverter).toList();
    }
    @PostMapping
    public Dto create(@RequestBody Dto entity) throws EntityAlreadyExistsException {
        return toDtoConverter.apply(service.create(toEntityConverter.apply(entity)));
    }
    @GetMapping("/{id}")
    public Dto readOne(@PathVariable Key id) throws EntityDoesNotExistsException {
        var entity = service.readByID(id);
        return toDtoConverter.apply(entity.orElseThrow(EntityDoesNotExistsException::new));
    }
    @PutMapping("/{id}")
    public void update(@RequestBody Dto entity, @PathVariable Key id) throws EntityDoesNotExistsException {
        service.update(toEntityConverter.apply(entity));
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Key id) throws EntityDoesNotExistsException {
        service.deleteByID(id);
    }

}
