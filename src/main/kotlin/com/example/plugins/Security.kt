package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.response.Output
import com.example.data.response.PassengerLogin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "http://0.0.0.0:8080/hello"
    val jwtDomain = "http://0.0.0.0:8080/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "login user"
    val jwtSecretAd = "login Admin"


//    val secret = config.property("jwt.secret").getString()
//    val issuer = config.property("jwt.issuer").getString()
//    val audience = config.property("jwt.audience").getString()
//    val myRealm = config.property("jwt.realm").getString()


    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience))
                    JWTPrincipal(credential.payload)
                else null
            }
            challenge { _, _ ->
                call.respond(Output("The token is invalid.",HttpStatusCode.Unauthorized.toString()))
            }

        }
        jwt("Admin") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecretAd))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience))
                    JWTPrincipal(credential.payload)
                else null
            }
            challenge { _, _ ->
                call.respond(Output("The token is invalid.",HttpStatusCode.Unauthorized.toString()))
            }
        }
    }
//    authentication{
//        jwt("Admin") {
//            realm = jwtRealm
//            verifier(
//                JWT
//                    .require(Algorithm.HMAC256("AdminLogin"))
//                    .withAudience(jwtAudience)
//                    .withIssuer(jwtDomain)
//                    .build()
//            )
//            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience))
//                    JWTPrincipal(credential.payload)
//                else null
//            }
//            challenge { _, _ ->
//                call.respond(HttpStatusCode.Unauthorized, "The token is invalid.")
//            }
//        }
//    }
}
