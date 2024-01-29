package site.goldenticket.domain.security.dto;

public record YanoljaUserResponse(
        Long id,
        String email,
        String name,
        String phoneNumber
) {
}
