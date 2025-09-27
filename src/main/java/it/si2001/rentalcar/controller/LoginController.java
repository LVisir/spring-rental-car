package it.si2001.rentalcar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    // just to show the endpoint inside the swagger
    @Operation(
            summary = "Login",
            description = "Send email and password to obtain JWT",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT generated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"access_token\": \"eyJhbGciOi...\" }")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Not valid credentials")
            }
    )
    @PostMapping
    public void fakeLogin(
            @RequestBody(
                    description = "Credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/x-www-form-urlencoded",
                            schema = @Schema(
                                    type = "object",
                                    example = "email=test@test.com&password=1234"
                            )
                    )
            )
            @RequestParam String email,
            @RequestParam String password
    ) {
        // managed by spring security
    }

}
