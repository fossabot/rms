package com.mamezou.rms.core.persistence.jpa;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.core.persistence.RentalItemRepository;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class RentalItemJpaRepository implements RentalItemRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public RentalItem get(int id) {
        return em.find(RentalItem.class, id);
    }

    @Override
    public List<RentalItem> findAll() {
        var jpql = "select r from RentalItem r order by r.id";
        return em.createQuery(jpql, RentalItem.class)
                    .getResultList();
    }

    @Override
    public RentalItem findBySerialNo(String serialNo) {
        var jpql = "select r from RentalItem r where r.serialNo = ?1";
        try {
            return em.createQuery(jpql, RentalItem.class)
                        .setParameter(1, serialNo)
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    public void add(@Valid RentalItem entity) {
        em.persist(entity);
        em.flush();
    }
}
