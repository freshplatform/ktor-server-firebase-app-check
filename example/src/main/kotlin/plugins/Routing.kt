package plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.freshplatform.ktor_server.firebase_app_check.utils.protectRouteWithAppCheck

class MissingEnvironmentVariableException(variableName: String) :
    RuntimeException("The required environment variable '$variableName' is missing.")

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World! this route is not using app firebase app check")
        }
        protectRouteWithAppCheck {
            route("/products") {
                get("/1") {
                    call.respondText { "Product 1, Firebase app check" }
                }
                get("/2") {
                    call.respondText { "Product 2, Firebase app check" }
                }
            }
        }
        route("/products") {
            get("/3") {
                call.respondText { "Product 2, Firebase app check is not required." }
            }
        }
        get("/test") {
            call.respondText { "This get /test doesn't use firebase app check!" }
        }
        protectRouteWithAppCheck {
            post("/test") {
                call.respondText { "This post /test is protected!" }
            }
        }
    }
}
