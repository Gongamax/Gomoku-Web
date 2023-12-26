import { LinkModel } from './LinkModel';

/**
 * Entity is a sub-entity that represents a resource.
 *
 * @property class is an array of strings that serves as an identifier for the link.
 * @property properties represent the properties of the entity.
 * @property links represent navigational links, distinct from entity relationships.
 * @property rel is the relationship of the link to its entity.
 * @property requireAuth is a boolean that indicates if the entity requires authentication.
 * */
export type EntityModel<T> = {
  class: string[];
  properties: T;
  links: LinkModel[];
  rel: string[];
  requireAuth: boolean;
};
