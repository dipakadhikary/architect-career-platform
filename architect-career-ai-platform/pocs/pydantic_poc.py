from typing import Optional

from pydantic import BaseModel, field_validator, model_validator
from pydantic.v1 import BaseSettings


class UserProfile(BaseModel):
    name: str
    age: int = 43
    email: str
    is_active: bool = True

    @field_validator('age')
    def check_age(cls, value):
        if value < 18:
            raise ValueError('Age must be at least 18')
        return value

user = UserProfile(name="Dipak", age=46, email="dipak@example.com")
print(user)

class User(BaseModel):
    password: str
    confirm_password: str

    @model_validator(mode='after')
    def passwords_match(cls, model):
        if model.password != model.confirm_password:
            raise ValueError("Passwords do not match")
        return model

User(password="a", confirm_password="a")

# Nested Models and Complex Data Structures
class Address(BaseModel):
    street: str
    city: str

class UserProfile(BaseModel):
    name: str
    age: int
    email: str
    address: Optional[Address] = None

address = Address(street="10 Rue de la Paix", city="Paris")
user = UserProfile(name="Emma Dubois", age=34, email="emma.dubois@example.fr", address=address)
print(user)

# Data Parsing and Serialization
data = '{"name": "Noah Kim", "age": 28, "email": "noah.kim@example.kr"}'
# user = UserProfile.model_validate_json(data)
# print(user)

# Serializing Models to JSON
user = UserProfile(name="Luca Rossi", age=29, email="luca.rossi@example.it")
json_data = user.model_dump_json()
print(json_data)

# Pydantic Settings Management
class Settings(BaseSettings):
    app_name: str
    debug: bool = False
    database_url: str

    class Config:
        env_file = ".env"

settings = Settings(app_name="TestApp", database_url="sqlite:///:memory:")
print(settings.app_name)

