package com.marlodev.app_android.model.order;


import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductResponse implements Parcelable {

    private Long id;
    private String name;
    private String description;

    private BigDecimal price;
    private BigDecimal oldPrice;
    private Integer discountPercent;

    private Boolean isNew;
    private Double rating;
    private Integer reviewsCount;

    private Integer categoryId;
    private Integer storeId;

    private String createdAt;
    private String updatedAt;

    // 🔹 Relacionados
//    private List<ProductVariantResponse> variants;
//    private List<ExtraResponse> extras;
//    private List<TagResponse> tags;

    // 🔹 Imágenes
    private List<String> imageUrls;
    private List<String> imagePublicIds;

    protected ProductResponse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();
        price = new BigDecimal(in.readString());
        oldPrice = new BigDecimal(in.readString());
        if (in.readByte() == 0) {
            discountPercent = null;
        } else {
            discountPercent = in.readInt();
        }
        byte tmpIsNew = in.readByte();
        isNew = tmpIsNew == 0 ? null : tmpIsNew == 1;
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        if (in.readByte() == 0) {
            reviewsCount = null;
        } else {
            reviewsCount = in.readInt();
        }
        if (in.readByte() == 0) {
            categoryId = null;
        } else {
            categoryId = in.readInt();
        }
        if (in.readByte() == 0) {
            storeId = null;
        } else {
            storeId = in.readInt();
        }
        createdAt = in.readString();
        updatedAt = in.readString();
        imageUrls = in.createStringArrayList();
        imagePublicIds = in.createStringArrayList();
    }

    public static final Creator<ProductResponse> CREATOR = new Creator<ProductResponse>() {
        @Override
        public ProductResponse createFromParcel(Parcel in) {
            return new ProductResponse(in);
        }

        @Override
        public ProductResponse[] newArray(int size) {
            return new ProductResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price.toString());
        dest.writeString(oldPrice.toString());
        if (discountPercent == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(discountPercent);
        }
        dest.writeByte((byte) (isNew == null ? 0 : isNew ? 1 : 2));
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        if (reviewsCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reviewsCount);
        }
        if (categoryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(categoryId);
        }
        if (storeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(storeId);
        }
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeStringList(imageUrls);
        dest.writeStringList(imagePublicIds);
    }
}