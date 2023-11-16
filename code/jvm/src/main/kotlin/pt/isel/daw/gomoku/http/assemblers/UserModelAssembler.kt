package pt.isel.daw.gomoku.http.assemblers

import de.ingogriebsch.spring.hateoas.siren.SirenModelBuilder
import de.ingogriebsch.spring.hateoas.siren.SirenModelBuilder.sirenModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import pt.isel.daw.gomoku.http.controllers.UsersController
import pt.isel.daw.gomoku.http.model.*

class UserModelAssembler {
    class GetUserModelAssembler : RepresentationModelAssembler<UserGetByIdOutputModel, RepresentationModel<*>> {
        /**
         * HINT Here we are creating a respective representation-model (based on the given source) with the [SirenModelBuilder].
         * This allows great flexibility in how to create the representation-model.
         */
        override fun toModel(source: UserGetByIdOutputModel): RepresentationModel<*> {
            val selfLink: Link =
                linkTo(methodOn(UsersController::class.java).getById(source.id.toString())).withSelfRel()
            return sirenModel()
                .classes("getUserByID")
                .title("Get user by ID")
                .properties(source)
                .linksAndActions(selfLink)
                .build()
        }
    }

    class GetRankingInfoModelAssembler : RepresentationModelAssembler<RankingInfoOutputModel, RepresentationModel<*>> {
        /**
         * HINT Here we are creating a respective representation-model (based on the given source) with the [SirenModelBuilder].
         * This allows great flexibility in how to create the representation-model.
         */
        override fun toModel(source: RankingInfoOutputModel): RepresentationModel<*> {
            val selfLink: Link =
                linkTo(methodOn(UsersController::class.java).getRankingInfo()).withSelfRel()
            return sirenModel()
                .classes("getRankingInfo")
                .title("Get ranking info")
                .properties(source)
                .linksAndActions(selfLink)
                .build()
        }
    }

    val getUserModelAssembler = GetUserModelAssembler()

    val getRankingInfoModelAssembler = GetRankingInfoModelAssembler()
}
