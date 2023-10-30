package it.almaviva.eai.zeebe.usertask.data.mapper;

import it.almaviva.eai.zeebe.usertask.data.entity.TaskDocument;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", imports = { Collectors.class, Optional.class, Collection.class,
        Stream.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ITaskMapper {

    TaskDomain map (TaskDocument taskEntity);

    List<TaskDomain> map(Iterable<TaskDocument> taskEntityIterable);
}
