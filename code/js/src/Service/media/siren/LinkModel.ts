
/**
 * Link is a navigational link, distinct from entity relationships.
 *
 * @property rel is the relationship of the link to its entity.
 * @property href is the URI of the linked resource.
 * */
export class LinkModel {
    public rel: string[];
    public href: string
}