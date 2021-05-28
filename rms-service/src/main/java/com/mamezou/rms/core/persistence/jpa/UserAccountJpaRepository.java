package com.mamezou.rms.core.persistence.jpa;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Update;
import com.mamezou.rms.core.persistence.UserAccountRepository;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class UserAccountJpaRepository implements UserAccountRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public UserAccount get(int id) {
        return em.find(UserAccount.class, id);
    }

    @Override
    public UserAccount findByLoginIdAndPasswod(String loginId, String password) {
        var jpql = "select u from UserAccount u where u.loginId = ?1 and u.password = ?2";
        try {
            return em.createQuery(jpql, UserAccount.class)
                        .setParameter(1, loginId)
                        .setParameter(2, password)
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public UserAccount findByLoginId(String loginId) {
        var jpql = "select u from UserAccount u where u.loginId = ?1";
        try {
            return em.createQuery(jpql, UserAccount.class)
                        .setParameter(1, loginId)
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<UserAccount> findAll() {
        var jpql = "select r from UserAccount r order by r.id";
        return em.createQuery(jpql, UserAccount.class)
                    .getResultList();
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    public void add(@Valid UserAccount entity) {
        em.persist(entity);
        em.flush();
    }

    @ValidateParam
    @ValidateGroup(groups = Update.class)
    @Override
    public UserAccount update(@Valid UserAccount entity) {
        if (!em.contains(entity) && get(entity.getId()) == null) {
            return null;
        }
        var updated = em.merge(entity);
        em.flush();
        return updated;
    }
}
