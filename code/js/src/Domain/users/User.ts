/**
 * `User` is a type that represents a user in the system.
 * It includes properties for `id`, `username`, `email`, and `passwordValidation`.
 *
 * @type {Object} User
 * @property {Id} id - The unique identifier of the user.
 * @property {string} username - The username of the user.
 * @property {Email} email - The email of the user.
 * @property {PasswordValidationInfo} passwordValidation - The password validation information of the user.
 */
export type User = {
  id : Id;
  username : string;
  email : Email;
  passwordValidation : PasswordValidationInfo
};

/**
 * `Email` is a type that represents an email.
 * It includes a `value` property which is a string.
 *
 * @type {Object} Email
 * @property {string} value - The email value.
 */
export type Email = {
  value : string;
}

/**
 * `Id` is a type that represents an identifier.
 * It includes a `value` property which is a number.
 *
 * @type {Object} Id
 * @property {number} value - The identifier value.
 */
export type Id = {
  value : number;
}

/**
 * `PasswordValidationInfo` is a type that represents password validation information.
 * It includes a `validationInfo` property which is a string.
 *
 * @type {Object} PasswordValidationInfo
 * @property {string} validationInfo - The password validation information.
 */
export type PasswordValidationInfo = {
  validationInfo : string;
}
