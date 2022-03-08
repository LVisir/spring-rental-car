package it.si2001.rentalcar.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter { // OncePerRequestFilter will intercept every request that come into the application
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // it will not intercept the request to this path because it means the user is trying to log in and in this place everyone can try to log in
        if(request.getServletPath().equals("/login")) {

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");

            // don't do anything, let the request go through
            filterChain.doFilter(request, response);

        }
        else {

            // the key in the header where all the authorization goes
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            // check if there is something in the authorization header and if it starts with "LoginToken " string
            // every request must have this token; this token specify the permission the user has to the server
            if(authorizationHeader != null && authorizationHeader.startsWith("LoginToken ")) {

                // it cut the initial string to have just the access_token signature
                String token = authorizationHeader.substring("LoginToken ".length());

                try{
                    // in the future, put in a bean or use the static method because u are using this also in CustomAuthenticationFilter
                    // secret specify how to decrypt/encrypt the access_token signature
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                    // verifier and decode the token:
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);

                    // if everything went well
                    String email = decodedJWT.getSubject();
                    String [] roles = decodedJWT.getClaim("roles").asArray(String.class); // must be the same what u put in CustomAuthenticationFilter

                    // collecting the roles
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                    // create the new token
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);

                    // giving to spring the user with his authorities, so it will understand which resources this user can access
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // forward the request
                    filterChain.doFilter(request, response);

                }catch (Exception e){

                    log.error("Error logging in : {}", e.getMessage());

                    response.setStatus(FORBIDDEN.value());

                    // return json with error -> error message:

                    Map<String, String> error = new HashMap<>();

                    error.put("error", e.getMessage());

                    response.setContentType(APPLICATION_JSON_VALUE);

                    new ObjectMapper().writeValue(response.getOutputStream(), error);

                }


            }
            else{ // otherwise, there isn't any token in the header

                // forward the request
                filterChain.doFilter(request, response);
            }

        }

    }
}
