import { EntityModel } from './EntityModel';
import { LinkModel } from './LinkModel';
import { ActionModel } from './ActionModel';

/**
 * Siren is a hypermedia specification for representing entities in JSON.
 *
 * @property class is an array of strings that serves as an identifier for the link.
 * @property properties represent the properties of the entity.
 * @property entities represent sub-entities of the entity.
 * @property actions represent the available actions on the entity.
 * @property links represent navigational links, distinct from entity relationships.
 * @property requireAuth is a boolean that indicates if the entity requires authentication.
 * */
export type SirenModel<T> = {
  class: string[];
  properties: T;
  links: LinkModel[];
  recipeLinks: LinkModel[];
  entities: EntityModel<T>[];
  actions: ActionModel[];
  requireAuth: boolean;
};

export const sirenMediaType = 'application/vnd.siren+json';
