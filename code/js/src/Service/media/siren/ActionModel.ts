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
export class ActionModel {
  public name: string;
  public href: string;
  public method: string;
  public type: string;
  public fields: FieldModel[];
  public requireAuth: boolean[];

  constructor(action: ActionModel) {
    this.name = action.name;
    this.href = action.href;
    this.method = action.method;
    this.type = action.type;
    this.fields = action.fields;
    this.requireAuth = action.requireAuth;	
  }
}
