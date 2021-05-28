package com.mamezou.rms.core.domain;

import static javax.persistence.AccessType.*;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mamezou.rms.core.domain.constraint.ItemName;
import com.mamezou.rms.core.domain.constraint.RmsId;
import com.mamezou.rms.core.domain.constraint.SerialNo;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Access(FIELD)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class RentalItem implements Transformable, IdAccessable {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;

    /** シリアル番号 */
    @SerialNo
    private String serialNo;

    /** 品名 */
    @ItemName
    private String itemName;


    public static RentalItem ofTransient(String serialNo, String itemName) {
        return of(null, serialNo, itemName);
    }

    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
