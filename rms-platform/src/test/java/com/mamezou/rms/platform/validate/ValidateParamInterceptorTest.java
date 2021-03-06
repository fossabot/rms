package com.mamezou.rms.platform.validate;

import static org.assertj.core.api.Assertions.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.groups.Default;
import javax.ws.rs.DELETE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.platform.validate.ValidateParamInterceptorTest.AnnotateVariationGroupDefTestBean;
import com.mamezou.rms.platform.validate.ValidateParamInterceptorTest.AnnotateVariationNoneGroupDefTestBean;
import com.mamezou.rms.platform.validate.ValidateParamInterceptorTest.GroupVariationTestBean;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.config.ConfigCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@DisableDiscovery
@AddExtension(ConfigCdiExtension.class)
@AddBean(value = ValidateParamInterceptor.class, scope = Dependent.class)
@AddBean(ValidateParamInterceptor.InnerValidatorImpl.class)
@AddBean(GroupVariationTestBean.class)
@AddBean(AnnotateVariationNoneGroupDefTestBean.class)
@AddBean(AnnotateVariationGroupDefTestBean.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class ValidateParamInterceptorTest {

    @Inject
    private GroupVariationTestBean testBean;

    // ValidationGroups for Test
    public interface Add {}
    public interface Update {}
    public interface Delete {}

    // ----------------------------------------------------- test methods

    @Test
    void testGroupVariationValidation() {

        TestEntity entity = new TestEntity();
        entity.setValue1(0);
        entity.setValue2(0);
        entity.setValue3(0);

        assertThatCode(
            () -> testBean.noneValidate(entity)
        ).doesNotThrowAnyException();

        ConstraintViolationException actual = catchThrowableOfType(() ->
            testBean.noneGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.defaultGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.addGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(3);

        actual = catchThrowableOfType(() ->
            testBean.updateGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.deleteGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            testBean.addAndDefaultGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(3);
    }

    @Inject
    private AnnotateVariationNoneGroupDefTestBean noneGroupAnnoteTestBean;

    @Test
    void testAnnotateVariationNoneGroupDefValidation() {

        TestEntity entity = new TestEntity();
        entity.setValue1(0);
        entity.setValue2(0);
        entity.setValue3(0);

        ConstraintViolationException actual = catchThrowableOfType(() ->
            noneGroupAnnoteTestBean.applyTypeDefValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            noneGroupAnnoteTestBean.defineTypeSameGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);

        actual = catchThrowableOfType(() ->
            noneGroupAnnoteTestBean.defineTypeDiffGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(3);

        actual = catchThrowableOfType(() ->
            noneGroupAnnoteTestBean.defineTypeDiffGroupAndValidateParamValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);
    }

    @Inject
    private AnnotateVariationGroupDefTestBean groupAnnoteTestBean;

    @Test
    void testAnnotateVariationGroupDefValidation() {

        TestEntity entity = new TestEntity();
        entity.setValue1(0);
        entity.setValue2(0);
        entity.setValue3(0);

        ConstraintViolationException actual = catchThrowableOfType(() ->
            groupAnnoteTestBean.applyTypeDefValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(3);

        actual = catchThrowableOfType(() ->
            groupAnnoteTestBean.defineTypeDiffGroupValidate(entity),
            ConstraintViolationException.class
        );
        assertThat(actual.getConstraintViolations()).hasSize(2);
    }


    // ----------------------------------------------------- inner classes for test

    public static class TestEntity {
        @Min(value = 100)
        private int value1;
        @Min(value = 100, groups = Add.class)
        private int value2;
        @Min(value = 100, groups = { Default.class, Update.class })
        private int value3;
        public int getValue1() {
            return value1;
        }
        public void setValue1(int value1) {
            this.value1 = value1;
        }
        public int getValue2() {
            return value2;
        }
        public void setValue2(int value2) {
            this.value2 = value2;
        }
        public int getValue3() {
            return value3;
        }
        public void setValue3(int value3) {
            this.value3 = value3;
        }
    }

    public static class GroupVariationTestBean {

        public void noneValidate(@Valid TestEntity entity) {
            // nop
        }

        @ValidateParam
        public void noneGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @ValidateParam
        @ValidateGroup(groups = Default.class)
        public void defaultGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @ValidateParam
        @ValidateGroup(groups = Add.class)
        public void addGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @ValidateParam
        @ValidateGroup(groups = Update.class)
        public void updateGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        @ValidateParam
        @ValidateGroup(groups = Delete.class)
        public void deleteGroupValidate(@Valid TestEntity entity) {
            // nop
        }
        @ValidateParam
        @ValidateGroup(groups = { Default.class, Add.class })
        public void addAndDefaultGroupValidate(@Valid TestEntity entity) {
            // nop
        }
    }

    @ValidateParam
    public static class AnnotateVariationNoneGroupDefTestBean {

        // ???????????????Group????????????
        public void applyTypeDefValidate(@Valid TestEntity entity) {
            // nop
        }

        // ?????????????????????????????????Group?????????
        @ValidateGroup(groups = Default.class)
        public void defineTypeSameGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        // ????????????????????????????????????Group?????????
        @ValidateGroup(groups = Add.class)
        public void defineTypeDiffGroupValidate(@Valid TestEntity entity) {
            // nop
        }

        // ????????????????????????????????????Group???ValidatePrama?????????
        @ValidateParam
        @ValidateGroup(groups = Update.class)
        public void defineTypeDiffGroupAndValidateParamValidate(@Valid TestEntity entity) {
            // nop
        }
    }

    @ValidateGroup(groups = Add.class)
    @ValidateParam
    public static class AnnotateVariationGroupDefTestBean {

        // ???????????????Group????????????
        public void applyTypeDefValidate(@Valid TestEntity entity) {
            // nop
        }

        // ????????????????????????????????????Group?????????(??????????????????)
        @ValidateGroup(groups = DELETE.class)
        public void defineTypeDiffGroupValidate(@Valid TestEntity entity) {
            // nop
        }
    }
}
