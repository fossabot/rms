package com.mamezou.rms.external.webapi;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Path;

import com.mamezou.rms.core.RentalReservationApplication;
import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.external.webapi.dto.AddRentalItemDto;
import com.mamezou.rms.external.webapi.dto.AddReservationDto;
import com.mamezou.rms.external.webapi.dto.AddUserAccountDto;
import com.mamezou.rms.external.webapi.dto.LoginDto;
import com.mamezou.rms.external.webapi.dto.RentalItemResourceDto;
import com.mamezou.rms.external.webapi.dto.ReservationResourceDto;
import com.mamezou.rms.external.webapi.dto.UserAccountResourceDto;
import com.mamezou.rms.platform.jwt.filter.Authenticated;
import com.mamezou.rms.platform.jwt.filter.GenerateToken;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

@Path("/rms")
@ApplicationScoped
@ValidateParam
public class RentalReservationResource implements EndPointSpec{

    private RentalReservationApplication application;

    @Inject
    public RentalReservationResource(RentalReservationApplication application) {
        this.application = application;
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(String loginId, String password) {
        return authenticate(LoginDto.of(loginId, password)); // this method is for debug so convert.
    }

    @GenerateToken
    @Override
    public UserAccountResourceDto authenticate(LoginDto loginDto) {
        return application.authenticate(loginDto.getLoginId(), loginDto.getPassword())
                    .transform(UserAccountResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @Override
    public List<ReservationResourceDto> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate date) {
        return application.findReservationByRentalItemAndStartDate(rentalItemId, date)
                .stream()
                .map(ReservationResourceDto::toDto)
                .collect(Collectors.toList());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @Override
    public List<ReservationResourceDto> findReservationByReserverId(Integer reserverId) {
        return application.findReservationByReserverId(reserverId)
                .stream()
                .map(ReservationResourceDto::toDto)
                .collect(Collectors.toList());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @Override
    public List<ReservationResourceDto> getOwnReservations() {
        return findReservationByReserverId(LoginUserUtils.get().getUserId());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @Override
    public List<RentalItemResourceDto> getAllRentalItems() {
        return application.getAllRetailItems()
                .stream()
                .map(RentalItemResourceDto::toDto)
                .collect(Collectors.toList());
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @ValidateGroup(groups = Add.class) // for @ReserveStartDateTimeFuture
    @Override
    public ReservationResourceDto addReservation(AddReservationDto addDto) {
        return application.addReservation(addDto.toEntity())
                .transform(ReservationResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed({ MEMBER_ROLE})
    @Override
    public void cancelReservation(Integer reservationId) {
        application.cancelReservation(reservationId);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public RentalItemResourceDto addRentalItem(AddRentalItemDto addDto) {
        return application.addRentalItem(addDto.toEntity())
                .transform(RentalItemResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto addUserAccount(AddUserAccountDto addDto) {
        return application.addUserAccount(addDto.toEntity())
                .transform(UserAccountResourceDto::toDto);
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public List<UserAccountResourceDto> getAllUserAccounts() {
        return application.getAllUserAccounts()
                .stream()
                .map(UserAccountResourceDto::toDto)
                .collect(Collectors.toList());
    }

    @Authenticated
    @RolesAllowed(ADMIN_ROLE)
    @Override
    public UserAccountResourceDto updateUserAccount(@Valid UserAccountResourceDto updateDto) {
        return application.updateUserAccount(updateDto.toEntity())
                .transform(UserAccountResourceDto::toDto);
    }
}
