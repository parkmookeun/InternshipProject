package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description="회원관련 API")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입 API",
            description = "사용자 정보를 입력받아 회원가입을 처리합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignUpResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "일반 사용자 등록 성공",
                                            summary = "일반 사용자 응답 예시",
                                            value = "{ \"username\": \"JIN HO\", \"nickname\": \"Mentos\", \"roles\": [ { \"role\": \"USER\" } ] }"
                                    ),
                                    @ExampleObject(
                                    name = "관리자 등록 성공",
                                    summary = "관리자 응답 예시",
                                    value = "{ \"username\": \"JIN HO ADMIN\", \"nickname\": \"Mentos\", \"roles\": [ { \"role\": \"ADMIN\" } ] }"
                            )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = "{ \"error\": { \"code\": \"USER_ALREADY_EXISTS\", \"message\": \"이미 가입된 사용자입니다.\" } }"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(
            @RequestBody SignUpRequestDto dto){

        SignUpResponseDto responseDto = userService.signUp(dto);
        return new ResponseEntity<SignUpResponseDto>(responseDto,HttpStatus.OK);
    }

    @Operation(
            summary = "로그인 API",
            description = "사용자 아이디와 비밀번호를 입력받아 로그인을 처리하고 JWT 토큰을 발급합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            summary = "로그인 성공 응답 예시",
                                            value = "{\n  \"token\": \"ekOIkdfjoak1dkfjpekdkcjdkoIOdj0KJDFOl1DkFJKL\"\n}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 실패",
                                            summary = "잘못된 계정 정보",
                                            value = "{\n  \"error\": {\n    \"code\": \"INVALID_CREDENTIALS\",\n    \"message\": \"아이디 또는 비밀번호가 올바르지 않습니다.\"\n  }\n}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto dto){

        LoginResponseDto responseDto = userService.login(dto);
        return new ResponseEntity<LoginResponseDto>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "사용자 권한 부여 API",
            description = "관리자가 특정 사용자에게 권한을 부여합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "권한 부여 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateRoleResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "권한 부여 성공",
                                            summary = "관리자 권한 부여 성공 응답 예시",
                                            value = "{\n  \"username\": \"JIN HO\",\n  \"nickname\": \"Mentos\",\n  \"roles\": [\n    {\n      \"role\": \"Admin\"\n    }\n  ]\n}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 부족한 경우(접근 제한)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "권한 부족",
                                            summary = "권한이 부족한 경우 응답 예시",
                                            value = "{\n  \"error\": {\n    \"code\": \"ACCESS_DENIED\",\n    \"message\": \"관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.\"\n  }\n}"
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<UpdateRoleResponseDto> updateRole(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader){

            UpdateRoleResponseDto responseDto = userService.updateRole(userId, authorizationHeader);

            return new ResponseEntity<UpdateRoleResponseDto>(responseDto, HttpStatus.OK);
    }
}
