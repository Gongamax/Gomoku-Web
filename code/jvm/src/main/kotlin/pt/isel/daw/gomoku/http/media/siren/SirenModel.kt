package pt.isel.daw.gomoku.http.media.siren

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpMethod
import org.springframework.web.util.UriTemplate
import java.net.URI

/**
 * Siren is a hypermedia specification for representing entities in JSON.
 *
 * @property clazz is an array of strings that serves as an identifier for the link.
 * @property properties represent the properties of the entity.
 * @property entities represent sub-entities of the entity.
 * @property actions represent the available actions on the entity.
 * @property links represent navigational links, distinct from entity relationships.
 * @property requireAuth is a boolean that indicates if the entity requires authentication.
 * */

data class SirenModel<T>(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val properties: T,
    val links: List<LinkModel>,
    val recipeLinks: List<LinkModel>,
    val entities: List<EntityModel<*>>,
    val actions: List<ActionModel>,
    val requireAuth: List<Boolean>
) {
    companion object {
        const val SIREN_MEDIA_TYPE = "application/vnd.siren+json"
    }
}

/**
 * Link is a navigational link, distinct from entity relationships.
 *
 * @property rel is the relationship of the link to its entity.
 * @property href is the URI of the linked resource.
 * */
data class LinkModel(
    val rel: List<String>,
    val href: String,
)

/**
 * Entity is a sub-entity that represents a resource.
 *
 * @property clazz is an array of strings that serves as an identifier for the link.
 * @property properties represent the properties of the entity.
 * @property links represent navigational links, distinct from entity relationships.
 * @property rel is the relationship of the link to its entity.
 * @property requireAuth is a boolean that indicates if the entity requires authentication.
 * */
data class EntityModel<T>(
    val clazz: List<String>,
    val properties: T,
    val links: List<LinkModel>,
    val rel: List<String>,
    val requireAuth: List<Boolean>
)

/**
 * Action is a set of instructions that can be carried out by the client.
 *
 * @property name is a string that identifies the action to be performed.
 * @property method is a string that identifies the protocol method to use.
 * @property href is the URI of the action.
 * @property type is the media type of the action.
 * @property fields represent the input fields of the action.
 * @property requireAuth is a boolean that indicates if the action requires authentication.
 * */

data class ActionModel(
    val name: String,
    val href: String,
    val method: String,
    val type: String,
    val fields: List<FieldModel>,
    val requireAuth: List<Boolean>
)

/**
 * Field is one of the input fields of the action.
 *
 * @property name is a string that identifies the field to be set.
 * @property type is the media type of the field.
 * @property value is the value of the field.
 * */

data class FieldModel(
    val name: String,
    val type: String,
    val value: String? = null,
)

/**
 *  This class is used to build a SirenModel.
 *  @param properties is the properties of the entity.
 *  @property clazz(value: String) adds a class to the list of classes.
 *  @property link(href: URI, rel: LinkRelation) adds a link to the list of links.
 *  @property entity(value: U, rel: LinkRelation, block: EntityBuilderScope<U>.() -> Unit) adds an entity to the list of entities.
 *  @property action(name: String, href: URI, method: HttpMethod, type: String, block: ActionBuilderScope.() -> Unit) adds an action to the list of actions.
 *  @property requireAuth(value: Boolean) adds a boolean to the list of booleans that indicates if the entity requires authentication.
 *  @property build() returns a SirenModel.
 * */

class SirenBuilderScope<T>(
    val properties: T,
) {
    private val links = mutableListOf<LinkModel>()
    private val entities = mutableListOf<EntityModel<*>>()
    private val classes = mutableListOf<String>()
    private val actions = mutableListOf<ActionModel>()
    private val requireAuth = mutableListOf<Boolean>()
    private val recipeLinks = mutableListOf<LinkModel>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun link(href: URI, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), href.toASCIIString()))
    }

    fun recipeLinks(href: UriTemplate, rel: LinkRelation) {
        recipeLinks.add(LinkModel(listOf(rel.value), href.toString()))
    }

    fun <U> entity(value: U, rel: LinkRelation, block: EntityBuilderScope<U>.() -> Unit) {
        val scope = EntityBuilderScope(value, listOf(rel.value))
        scope.block()
        entities.add(scope.build())
    }

    fun action(name: String, href: URI, method: HttpMethod, type: String, block: ActionBuilderScope.() -> Unit) {
        val scope = ActionBuilderScope(name, href, method, type)
        scope.block()
        actions.add(scope.build())
    }

    fun requireAuth(value: Boolean) {
        requireAuth.add(value)
    }

    fun build(): SirenModel<T> = SirenModel(
        clazz = classes,
        properties = properties,
        links = links,
        recipeLinks = recipeLinks,
        entities = entities,
        actions = actions,
        requireAuth = requireAuth
    )
}

/**
 *  This class is used to build an EntityModel.
 *  @param properties is the properties of the entity.
 *  @param rel is the relationship of the link to its entity.
 *  @property clazz(value: String) adds a class to the list of classes.
 *  @property link(href: URI, rel: LinkRelation) adds a link to the list of links.
 *  @property requireAuth(value: Boolean) adds a boolean to the list of booleans that indicates if the entity requires authentication.
 *  @property build() returns an EntityModel.
 * */

class EntityBuilderScope<T>(
    val properties: T,
    val rel: List<String>,
) {
    private val links = mutableListOf<LinkModel>()
    private val classes = mutableListOf<String>()
    private val requireAuth = mutableListOf<Boolean>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun link(href: URI, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), href.toASCIIString()))
    }

    fun requireAuth(value: Boolean) {
        requireAuth.add(value)
    }

    fun build(): EntityModel<T> = EntityModel(
        clazz = classes,
        properties = properties,
        links = links,
        rel = rel,
        requireAuth = requireAuth
    )
}

/**
 *  This class is used to build an ActionModel.
 *  @param name is a string that identifies the action to be performed.
 *  @param href is the URI of the action.
 *  @param method is a string that identifies the protocol method to use.
 *  @param type is the media type of the action.
 * @property textField(name: String) adds a text field to the list of fields.
 * @property numberField(name: String) adds a number field to the list of fields.
 * @property hiddenField(name: String, value: String) adds a hidden field to the list of fields.
 * @property requireAuth(value: Boolean) adds a boolean to the list of booleans that indicates if the action requires authentication.
 * @property build() returns an ActionModel.
 * */

class ActionBuilderScope(
    private val name: String,
    private val href: URI,
    private val method: HttpMethod,
    private val type: String,
) {
    private val fields = mutableListOf<FieldModel>()
    private val requireAuth = mutableListOf<Boolean>()

    fun textField(name: String) {
        fields.add(FieldModel(name, "text"))
    }

    fun numberField(name: String) {
        fields.add(FieldModel(name, "number"))
    }

    fun hiddenField(name: String, value: String) {
        fields.add(FieldModel(name, "hidden", value))
    }

    fun requireAuth(value: Boolean) {
        requireAuth.add(value)
    }

    fun build() = ActionModel(name, href.toASCIIString(), method.name(), type, fields, requireAuth)
}

/**
 * Creates a SirenModel.
 * @param value is the properties of the entity.
 * @param block is the scope of the SirenModel.
 * */

fun <T> siren(value: T, block: SirenBuilderScope<T>.() -> Unit): SirenModel<T> {
    val scope = SirenBuilderScope(value)
    scope.block()
    return scope.build()
}