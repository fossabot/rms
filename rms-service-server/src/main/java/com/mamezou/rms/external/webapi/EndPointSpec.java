package com.mamezou.rms.external.webapi;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.mamezou.rms.core.domain.constraint.LoginId;
import com.mamezou.rms.core.domain.constraint.Passowrd;
import com.mamezou.rms.core.domain.constraint.RmsId;
import com.mamezou.rms.external.webapi.dto.AddRentalItemDto;
import com.mamezou.rms.external.webapi.dto.AddReservationDto;
import com.mamezou.rms.external.webapi.dto.AddUserAccountDto;
import com.mamezou.rms.external.webapi.dto.LoginDto;
import com.mamezou.rms.external.webapi.dto.RentalItemResourceDto;
import com.mamezou.rms.external.webapi.dto.ReservationResourceDto;
import com.mamezou.rms.external.webapi.dto.UserAccountResourceDto;

/**
 * レンタル予約システムのREST APIインタフェース。
 * MicroProfileのOpenAPIのアノテーションを使いAPIの詳細情報を付加している。<br>
 * 全体に関するAPI情報は{@link ApplicationConfig}に定義。
 */
public interface EndPointSpec {

    /** for @RolesAllowed const */
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String MEMBER_ROLE = "MEMBER";

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Authenticate API")
    @Operation(
        operationId = "authenticate",
        summary = "ユーザ認証を行う（curlのテスト用）",
        description = "ログイン名とパスワードに一致するユーザを取得する")
    @Parameters({
        @Parameter(name = "loginId", description = "ログインId", required = true,
            schema = @Schema(implementation = String.class, minLength = 5, maxLength = 10)),
        @Parameter(name = "password",  description = "パスワード", required = true,
            schema = @Schema(implementation = String.class, minLength = 5, maxLength = 10))
    })
    @APIResponse(
        responseCode = "200",
        description = "認証成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    UserAccountResourceDto authenticate(
                @LoginId @QueryParam("loginId") String loginId,
                @Passowrd @QueryParam("password") String password);


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Authenticate API")
    @Operation(
        operationId = "authenticate",
        summary = "ユーザ認証を行う",
        description = "ログイン名とパスワードに一致するユーザを取得する")
    @Parameter(
            name = "loginDto", description = "ログインIDとパスワード", required = true,
            content = @Content(mediaType = "application/json",
            schema = @Schema(type = SchemaType.OBJECT, implementation = LoginDto.class)))
    @APIResponse(
        responseCode = "200",
        description = "認証成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    UserAccountResourceDto authenticate(@Valid LoginDto loginDto);

    @GET
    @Path("/reservations/item/{itemId}/startdate/{startDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "findReservationByRentalItemAndStartDate",
        summary = "指定されたレンタル品と利用開始日で予約を検索する",
        description = "指定されたレンタル品と利用開始日に一致する予約を検索する")
    @Parameters({
        @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true),
        @Parameter(name = "startDate", description = "利用開始日", in = ParameterIn.PATH, required = true,
            schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    })
    @APIResponse(responseCode = "200",
        description = "検索結果",
        content = @Content(mediaType = "application/json", schema =
            @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/NotFound")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findReservationByRentalItemAndStartDate(
                @RmsId @PathParam("itemId") Integer itemId,
                @NotNull @PathParam("startDate") LocalDate startDate);


    @GET
    @Path("/reservations/reserver/{reserverId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "findReservationByReserverId",
        summary = "指定されたユーザが予約者の予約を検索する",
        description = "指定されたユーザが予約者の予約を検索する")
    @Parameters({
        @Parameter(name = "reserverId", description = "ユーザID", in = ParameterIn.PATH, required = true)
    })
    @APIResponse(responseCode = "200",
        description = "検索結果",
        content = @Content(mediaType = "application/json", schema =
            @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findReservationByReserverId(
                @RmsId @PathParam("reserverId") Integer reserverId);

    @GET
    @Path("/reservations/own")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "getOwnReservations",
        summary = "自分の予約一覧を取得する",
        description = "ログインユーザが予約者となっている予約の一覧を取得する。このAPIは/reservations/?reserverId={reserverId}のエイリアスとなっている")
    @APIResponse(responseCode = "200",
        description = "検索結果",
        content = @Content(mediaType = "application/json", schema =
            @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    List<ReservationResourceDto> getOwnReservations();

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "getAllRentalItems",
        summary = "レンタル品の全件を取得する",
        description = "登録されているすべてのレンタル品を取得する")
    @APIResponse(responseCode = "200",
        description = "検索結果",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = RentalItemResourceDto.class)))
    List<RentalItemResourceDto> getAllRentalItems();


    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "getAllUserAccounts",
        summary = "ユーザの全件を取得する",
        description = "登録されているすべてのユーザを取得する")
    @APIResponse(responseCode = "200",
        description = "検索結果",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = UserAccountResourceDto.class)))
    List<UserAccountResourceDto> getAllUserAccounts();

    @POST
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Member API")
    @Operation(
        operationId = "addReservation",
        summary = "予約を登録する",
        description = "依頼された予約内容を登録する")
    @Parameter(
        name = "addReservationDto", description = "登録内容", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(type = SchemaType.OBJECT, implementation = AddReservationDto.class)))
    @APIResponse(
        responseCode = "200",
        description = "登録成功",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    ReservationResourceDto addReservation(@Valid AddReservationDto dto);

    @DELETE
    @Path("/reservations/{reservationId}")
    @Tag(name = "Member API")
    @Operation(
        operationId = "cancelReservation",
        summary = "予約をキャンセルする",
        description = "依頼された予約IDに対する予約をキャンセルする。予約のキャンセルは予約した人しか行えない。"
                + "他の人が予約キャンセルを行った場合は禁止操作としてエラーにする")
    @Parameter(
        name = "itemId", description = "予約ID", in = ParameterIn.PATH, required = true)
    @APIResponse(
        responseCode = "200",
        description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/Forbidden")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    void cancelReservation(@RmsId @PathParam("reservationId") Integer reservationId);

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Admin API")
    @Operation(
        operationId = "addRentalItem",
        summary = "レンタル品を登録する",
        description = "依頼されたレンタル品を登録する")
    @Parameter(
        name = "addRentalItemDto", description = "登録内容", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(type = SchemaType.OBJECT, implementation = AddRentalItemDto.class)))
    @APIResponse(
        responseCode = "200",
        description = "登録成功",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = RentalItemResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    RentalItemResourceDto addRentalItem(@Valid AddRentalItemDto dto);

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Admin API")
    @Operation(
        operationId = "addUserAccount",
        summary = "ユーザを登録する",
        description = "依頼されたユーザを登録する")
    @Parameter(
        name = "addUserAccountDto", description = "登録内容", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(type = SchemaType.OBJECT, implementation = AddUserAccountDto.class)))
    @APIResponse(
        responseCode = "200",
        description = "登録成功",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    UserAccountResourceDto addUserAccount(@Valid AddUserAccountDto dto);

    @PUT
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Admin API")
    @Operation(
        operationId = "updateUserAccount",
        summary = "ユーザを更新する",
        description = "依頼されたユーザを更新する")
    @Parameter(
        name = "dto", description = "更新内容", required = true,
        content = @Content(mediaType = "application/json",
        schema = @Schema(type = SchemaType.OBJECT, implementation = UserAccountResourceDto.class)))
    @APIResponse(
        responseCode = "200",
        description = "登録成功",
        content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.OBJECT, implementation = UserAccountResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    UserAccountResourceDto updateUserAccount(@Valid UserAccountResourceDto dto);
}
