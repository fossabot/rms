package com.mamezou.rms.external.webapi;

import static com.mamezou.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.common.ServiceLoginUser;
import com.mamezou.rms.core.domain.UserAccount.UserType;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.external.webapi.RentalReservationResourceTest.LoginUserInterceptor;
import com.mamezou.rms.external.webapi.dto.AddRentalItemDto;
import com.mamezou.rms.external.webapi.dto.AddReservationDto;
import com.mamezou.rms.external.webapi.dto.AddUserAccountDto;
import com.mamezou.rms.external.webapi.dto.RentalItemResourceDto;
import com.mamezou.rms.external.webapi.dto.ReservationResourceDto;
import com.mamezou.rms.external.webapi.dto.UserAccountResourceDto;
import com.mamezou.rms.external.webapi.mapper.GenericErrorInfo;
import com.mamezou.rms.external.webapi.mapper.ValidationErrorInfo;
import com.mamezou.rms.platform.provider.converter.RmsTypeParameterFeature;
import com.mamezou.rms.platform.validate.ValidateParam;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = false)
@AddConfig(key = "server.port", value = "7001")
@AddConfig(key = "jwt.filter.enable", value = "false") // 認証認可OFF
@AddBean(value = LoginUserInterceptor.class, scope = Dependent.class) // test用のユーザを設定
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class RentalReservationResourceTest {

    /*
     * NOTE:
     * 参照系テストに副作用がないように更新系テストは参照系の後に実行resetPerTest = trueにすれば
     * 副作用なくテストメソッドを実行できるがネットワーク越しのテストで時間が掛かるため、敢えて
     * resetPerTest = falseにしている
     */
    private static final int WITHOUT_UPDATE_ORDER = 1;
    private static final int WITH_UPDATE_ORDER = 999;

    private static ServiceLoginUser testUser = ServiceLoginUser.UNKNOWN_USER;

    private EndPointSpec endPoint;

//    static {
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "8888");
//    }

    @Interceptor
    @Priority(Interceptor.Priority.APPLICATION)
    @ValidateParam // @InterceptorBindingとして便宜的に利用
    public static class LoginUserInterceptor {
        @AroundInvoke
        public Object obj(InvocationContext ic) throws Exception {
            try {
                LoginUserUtils.set(testUser);
                return ic.proceed();
            } finally {
                LoginUserUtils.remove();
            }
        }
    }

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                //.baseUri(new URI("http://pmr216n.primo.mamezou.com:7001/rms"))
                .baseUri(new URI("http://localhost:7001/rms"))
                .register(RmsTypeParameterFeature.class)
                .build(EndPointSpec.class);
        testUser = ServiceLoginUser.UNKNOWN_USER;
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAuthenticate() {
        var expect = userAccountDto1();
        var actual = endPoint.authenticate("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAuthenticatePasswordUnmatch() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.authenticate("member1", "member9999"),  // password不一致
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAuthenticateParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.authenticate("123", "123"),  // id, password桁数不足
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 2);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByRentalItemAndStartDate() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2,
                        rentalItemDto3(), userAccountDto2())
                );
        var actual = endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByRentalItemAndStartDateNotFound() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemAndStartDate(3, LocalDate.of(2019, 4, 1)), // 該当なし
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByRentalItemAndStartDateParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.findReservationByRentalItemAndStartDate(-1, LocalDate.of(2020, 4, 1)), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 1);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByReserverId() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        var actual = endPoint.findReservationByReserverId(1);
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByReserverIdNotFound() {
        var actual = endPoint.findReservationByReserverId(9);
        assertThatToString(actual).isEmpty();
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testFindReservationByReserverIdParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                endPoint.findReservationByReserverId(-1), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 1);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testGetOwnReservations() {
        var expect = List.of(
                newReservationDto(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1,
                        rentalItemDto3(), userAccountDto1()),
                newReservationDto(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1,
                        rentalItemDto3(), userAccountDto1())
                );
        testUser = ServiceLoginUser.of(1, null); // 事前条件の設定(Interceptorで設定するユーザ)
        var actual = endPoint.getOwnReservations();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testGetAllRentalItems() {
        var expect = List.of(
                newRentalItemDto(1, "A0001", "レンタル品1号"),
                newRentalItemDto(2, "A0002", "レンタル品2号"),
                newRentalItemDto(3, "A0003", "レンタル品3号"),
                newRentalItemDto(4, "A0004", "レンタル品4号")
                );
        var actual = endPoint.getAllRentalItems();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }


    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testGetAllUserAccounts() {
        var expected = List.of(
                newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                newUserAccountResourceDto(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                newUserAccountResourceDto(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actuals = endPoint.getAllUserAccounts();
        assertThatToString(actuals).containsExactlyElementsOf(expected);
    }

    @Test
    @Order(WITH_UPDATE_ORDER)
    void testAddReservation() {
        var expect = newReservationDto(4, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ4", 3, 1,
                rentalItemDto3(), userAccountDto1());
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ4", 3, 1);
        var actual = endPoint.addReservation(addReservation);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddReservationParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addReservation(new AddReservationDto()), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 4);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddReservationCorrelationCheckError() {
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 12, 0, 0), LocalDateTime.of(2099, 4, 1, 10, 0, 0), "メモ4", 3, 1);
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addReservation(addReservation), // startDateTime < EndDateTime エラー
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 1);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddReservationTargetNotFound() {
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ4", 999, 1);
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addReservation(addReservation), // 該当なし
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class);
    }

    @Test
    @Order(WITH_UPDATE_ORDER + 1) // testAddReservation()を実行後
    void testAddReservationDuplicateData() {
        var addReservation = newAddReservationDto(LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 13, 0, 0), "メモ4", 3, 1); // 期間重複あり
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addReservation(addReservation), // 期間重複
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class);
    }

    @Test
    @Order(WITH_UPDATE_ORDER)
    void testAddRentalItem() {
        var expect = newRentalItemDto(5, "A0005", "レンタル品5号");
        var addRentalItem = newAddRentalItemDto("A0005", "レンタル品5号");
        var actual = endPoint.addRentalItem(addRentalItem);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddRentalItemParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addRentalItem(new AddRentalItemDto()), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 1);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddRentalItemDuplicateData() {
        var addRentalItem = newAddRentalItemDto("A0004", "レンタル品5号"); // SerialNo重複
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addRentalItem(addRentalItem), // SerialNo重複
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class);
    }

    @Test
    @Order(WITH_UPDATE_ORDER)
    void testAddUserAccount() {
        var expect = newUserAccountResourceDto(4, "member3", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER);
        var addUserAccount = newAddUserAccountDto("member3", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER);
        var actual = endPoint.addUserAccount(addUserAccount);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddUserAccountParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addUserAccount(new AddUserAccountDto()), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 4); // 未入力4件
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testAddUserAccountDuplicateData() {
        var addUserAccount = newAddUserAccountDto("member2", "password3", "name3", "090-0000-0000", "連絡先4", UserType.MEMBER); // loginId重複
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.addUserAccount(addUserAccount),
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.CONFLICT, BusinessFlowException.class);
    }

    @Test
    @Order(WITH_UPDATE_ORDER + 10) // testAddReservationXX()のテストをすべて実行後
    void testCancelReservation() {
        testUser = ServiceLoginUser.of(2, null); // 事前条件の設定(Interceptorで設定するユーザ)
        var ownReservationSize = endPoint.findReservationByReserverId(2).size();
        endPoint.cancelReservation(2); // reservation.id=2, reservation.userAccountId=2を削除
        var ownReservations = endPoint.findReservationByReserverId(2);
        assertThatToString(ownReservations).hasSize(ownReservationSize -1); // 1件削除されていること
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testCancelReservationParameterError() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.cancelReservation(-1), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 1);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testCancelReservationTargetNotFound() {
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.cancelReservation(999), // 該当なし
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testCancelReservationForbidden() {
        testUser = ServiceLoginUser.of(2, null); // 事前条件の設定(Interceptorで設定するユーザ)
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.cancelReservation(1), // reservation.id=1の予約者はid=1のユーザなので消せないハズ
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.FORBIDDEN, BusinessFlowException.class);
    }

    @Test
    @Order(WITH_UPDATE_ORDER)
    void testUpdateUserAccount() {
        var updateUser = newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = endPoint.updateUserAccount(updateUser);
        assertThatToString(actual).isEqualTo(updateUser);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testUpdateUserAccountTargetNotFound() {
        var updateUser = newUserAccountResourceDto(999, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.updateUserAccount(updateUser), // 該当なし
                    WebApplicationException.class
                    );
        assertGenericErrorInfo(actual, Status.NOT_FOUND, BusinessFlowException.class);
    }

    @Test
    @Order(WITHOUT_UPDATE_ORDER)
    void testUpdateUserAccountParameterError() {
        var updateUser = newUserAccountResourceDto(null, null, null, "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        WebApplicationException actual =
                catchThrowableOfType(() ->
                    endPoint.updateUserAccount(updateUser), // parameter error
                    WebApplicationException.class
                    );
        assertValidationErrorInfo(actual, 3); // 未入力3件
    }

    private void assertGenericErrorInfo(WebApplicationException actual, Status expectedStatus, Class<? extends Exception> expectedClass) {
        assertThat(actual.getResponse().getStatus()).isEqualTo(expectedStatus.getStatusCode());
        assertThat(actual.getResponse().getHeaderString("Rms-Exception")).isEqualTo(expectedClass.getSimpleName());

        GenericErrorInfo errorInfo = actual.getResponse().readEntity(GenericErrorInfo.class);
        assertThat(errorInfo.getErrorMessage()).isNotEmpty();
        assertThat(errorInfo.getErrorReason()).isEqualTo(expectedClass.getSimpleName());
    }

    private void assertValidationErrorInfo(WebApplicationException actual, int expectedErrorSize) {
        assertThat(actual.getResponse().getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        assertThat(actual.getResponse().getHeaderString("Rms-Exception")).isEqualTo(ConstraintViolationException.class.getSimpleName());

        ValidationErrorInfo errorInfo = actual.getResponse().readEntity(ValidationErrorInfo.class);
        assertThat(errorInfo.getErrorMessage()).isNotEmpty();
        assertThat(errorInfo.getErrorReason()).isEqualTo(ConstraintViolationException.class.getSimpleName());
        assertThat(errorInfo.getErrorItems()).hasSize(expectedErrorSize);
    }

    private ReservationResourceDto newReservationDto(Integer id, LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            Integer rentalItemId, Integer userAccountId, RentalItemResourceDto rentalItemDto, UserAccountResourceDto userAccountDto) {
        var dto = new ReservationResourceDto();
        dto.setId(id);
        dto.setStartDateTime(startDateTime);
        dto.setEndDateTime(endDateTime);
        dto.setNote(note);
        dto.setRentalItemId(rentalItemId);
        dto.setUserAccountId(userAccountId);
        dto.setRentalItemDto(rentalItemDto);
        dto.setUserAccountDto(userAccountDto);
        return dto;
    }

    private RentalItemResourceDto newRentalItemDto(Integer id, String serialNo, String itemName) {
        var dto = new RentalItemResourceDto();
        dto.setId(id);
        dto.setSerialNo(serialNo);
        dto.setItemName(itemName);
        return dto;
    }

    private UserAccountResourceDto newUserAccountResourceDto(Integer id, String loginId, String password, String userName, String phoneNumber,
            String contact, UserType userType) {
        var dto = new UserAccountResourceDto();
        dto.setId(id);
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setUserName(userName);
        dto.setPhoneNumber(phoneNumber);
        dto.setContact(contact);
        dto.setUserType(userType.name());
        return dto;
    }

    private AddReservationDto newAddReservationDto(LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            Integer rentalItemId, Integer userAccountId) {
        var dto = new AddReservationDto();
        dto.setStartDateTime(startDateTime);
        dto.setEndDateTime(endDateTime);
        dto.setNote(note);
        dto.setRentalItemId(rentalItemId);
        dto.setUserAccountId(userAccountId);
        return dto;
    }

    private AddRentalItemDto newAddRentalItemDto(String serialNo, String itemName) {
        var dto = new AddRentalItemDto();
        dto.setSerialNo(serialNo);
        dto.setItemName(itemName);
        return dto;
    }

    private AddUserAccountDto newAddUserAccountDto(String loginId, String password, String userName, String phoneNumber, String contact, UserType userType) {
        var dto = new AddUserAccountDto();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setUserName(userName);
        dto.setPhoneNumber(phoneNumber);
        dto.setContact(contact);
        dto.setUserType(userType);
        return dto;
    }

    private RentalItemResourceDto rentalItemDto3() {
        return newRentalItemDto(3, "A0003", "レンタル品3号");
    }

    private UserAccountResourceDto userAccountDto1() {
        return newUserAccountResourceDto(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
    }

    private UserAccountResourceDto userAccountDto2() {
        return newUserAccountResourceDto(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER);
    }
}