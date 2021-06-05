package com.mamezou.rms.core.persistence.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.mamezou.rms.core.domain.IdAccessable;
import com.mamezou.rms.core.domain.Transformable;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.core.persistence.GenericRepository;
import com.mamezou.rms.core.persistence.file.converter.EntityArrayConverter;
import com.mamezou.rms.core.persistence.file.io.FileAccessor;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

public class AbstractFileRepository<T extends Transformable & IdAccessable> implements GenericRepository<T>, FileRepository {

    private FileAccessor fileAccessor;
    private EntityArrayConverter<T> entityConverter;


    // ----------------------------------------------------- constructor methods

    public AbstractFileRepository(FileAccessor fileAccessor, EntityArrayConverter<T> entityConverter) {
        this.fileAccessor = fileAccessor;
        this.entityConverter = entityConverter;
    }


    // ----------------------------------------------------- implement methods

    @Override
    public T get(int id) {
        return load().stream()
                .filter(items -> Integer.parseInt(items[0]) == id) // numberはpos:0は共通
                .map(entityConverter::toEntity)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> findAll() {
        return load().stream()
                .map(entityConverter::toEntity)
                .collect(Collectors.toList());
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    public void add(@Valid T entity) {
        var nextSeq = this.getNextSequence();
        entity.setId(nextSeq);
        save(entity.transform(entityConverter::toArray));
    }

    @Override
    public Path getStoragePath() {
        return fileAccessor.getFilePath();
    }


    // ----------------------------------------------------- specific methods

    public int getNextSequence() {
        return load().stream()
                .map(items -> Integer.parseInt(items[0]))
                .collect(Collectors.maxBy(Integer::compareTo))
                .orElse(0)
                + 1;
    }

    public void delete(Integer id) {
        var excludedData = load().stream()
                .filter(items -> Integer.parseInt(items[0])  != id) // numberはpos:0は共通
                .collect(Collectors.toList());
        saveAll(excludedData);
    }


    // ----------------------------------------------------- package private methods

    EntityArrayConverter<T> getConverter() {
        return entityConverter;
    }

    List<String[]> load() {
        try {
            List<String[]> dataList = new ArrayList<>();
            fileAccessor.load(dataList);
            return dataList;
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    void save(String[] arrayData) {
        try {
            fileAccessor.save(arrayData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    void saveAll(List<String[]> allData) {
        try {
            fileAccessor.saveAll(allData);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }
}
