export type User = {
  id : Id;
  username : string;
  email : Email;
  passwordValidation : PasswordValidationInfo
};

export type Email = {
  value : string;
}

export type Id = {
  value : number;
}

export type PasswordValidationInfo = {
  validationInfo : string;
}
