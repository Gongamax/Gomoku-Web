import { FieldModel } from './FieldModel';

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
export type ActionModel = {
  name: string;
  href: string;
  method: string;
  type: string;
  fields: FieldModel[];
  requireAuth: boolean;
};
