package pt.isel.daw.gomoku.http.assemblers

import de.ingogriebsch.spring.hateoas.siren.SirenModelBuilder.sirenModel
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.Page
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component
import pt.isel.daw.gomoku.http.controllers.UsersController
import pt.isel.daw.gomoku.http.model.*

/*class UserModelAssembler(val pagedResourcesAssembler: PagedResourcesAssembler<StatsOutputModel>) {
    inner class GetUserModelAssembler : RepresentationModelAssembler<UserGetByIdOutputModel, RepresentationModel<*>> {
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

    *//** getRankingInfo retorna um conjunto de dados, por isso deve-se usar uma collection *//*
    inner class GetAllStatsModelAssembler() : RepresentationModelAssembler<StatsOutputModel, RepresentationModel<*>> {
        override fun toModel(source: StatsOutputModel): RepresentationModel<*> {
            *//*val selfLink: Link =
                linkTo(methodOn(UsersController::class.java).getAllStats()).withSelfRel()*//*
            return sirenModel()
                .classes("getRankingInfo")
                .title("Get ranking info")
                .properties(source)
            //.linksAndActions(selfLink)
                .build()
        }

        fun toPagedModel(page: Page<StatsOutputModel>): PagedModel<RepresentationModel<*>> {
            return pagedResourcesAssembler.toModel(page, this)
        }
    }

    val getUserModelAssembler = GetUserModelAssembler()

    val getAllStatsModelAssembler = GetAllStatsModelAssembler()
}*/

class GetAllStatsModelAssembler(val pagedResourcesAssembler: PagedResourcesAssembler<StatsOutputModel>) : RepresentationModelAssembler<StatsOutputModel, RepresentationModel<*>> {
    override fun toModel(source: StatsOutputModel): RepresentationModel<*> {
        val selfLink: Link =
            linkTo(methodOn(UsersController::class.java).getStatsById(source.id.toString())).withSelfRel()
        return sirenModel()
            .classes("getAllStats")
            .title("Get all stats")
            .properties(source)
            .linksAndActions(selfLink)
            .build()
    }

    fun toPagedModel(page: Page<StatsOutputModel>): PagedModel<RepresentationModel<*>> {
        return pagedResourcesAssembler.toModel(page, this)
    }
}

class GetUserModelAssembler : RepresentationModelAssembler<UserGetByIdOutputModel, RepresentationModel<*>> {
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
