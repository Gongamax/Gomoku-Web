
/**
 * Field is one of the input fields of the action.
 *
 * @property name is a string that identifies the field to be set.
 * @property type is the media type of the field.
 * @property value is the value of the field.
 */
export class FieldModel {
  public name: string;
  public type: string;
  public value: string;

  constructor(field: FieldModel) {
    this.name = field.name;
    this.type = field.type;
    this.value = field.value;
  }
}
