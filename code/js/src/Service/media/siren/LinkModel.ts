/**
 * Link is a navigational link, distinct from entity relationships.
 *
 * @property rel is the relationship of the link to its entity.
 * @property href is the URI of the linked resource.
 * */
export type LinkModel = {
  rel: string[];
  href: string;
};
