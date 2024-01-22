package site.goldenticket.domain.product.dto;

import site.goldenticket.domain.chat.dto.response.ProgressChatResponse;
import site.goldenticket.domain.product.constants.ProgressProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.dummy.reservation.constants.ReservationType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Comparator;

public record ProductProgressHistoryResponse(
		Long productId,
		String accommodationImage,
		String accommodationName,
		ReservationType reservationType,
		String roomName,
		int standardNumber,
		int maximumNumber,
		LocalTime checkInTime,
		LocalTime checkOutTime,
		LocalDate checkInDate,
		LocalDate checkOutDate,
		int originPrice,
		int yanoljaPrice,
		int goldenPrice,
		ProgressProductStatus status,
		List<ProgressChatResponse> chats
) {

	public static ProductProgressHistoryResponse fromEntity(
			Product product,
			ProgressProductStatus progressProductStatus,
			List<ProgressChatResponse> progressChatResponseList
	) {

		progressChatResponseList.sort(Comparator.comparing(ProgressChatResponse::chatRoomStatus));

		return new ProductProgressHistoryResponse(
				product.getId(),
				product.getAccommodationImage(),
				product.getAccommodationName(),
				product.getReservationType(),
				product.getRoomName(),
				product.getStandardNumber(),
				product.getMaximumNumber(),
				product.getCheckInTime(),
				product.getCheckOutTime(),
				product.getCheckInDate(),
				product.getCheckOutDate(),
				product.getOriginPrice(),
				product.getYanoljaPrice(),
				product.getGoldenPrice(),
				progressProductStatus,
				progressChatResponseList
		);
	}
}
