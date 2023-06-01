from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin
from django.db import models


class UserManager(BaseUserManager):
    def create_user(self, username, full_name, password=None):
        user = self.model(
            username=username,
            full_name=full_name,
        )

        user.set_password(password)
        user.save(using=self._db)

        return user

    def create_superuser(self, username, full_name, password):
        user = self.create_user(
            username=username,
            full_name=full_name,
            password=password,
        )
        user.is_staff = True
        user.is_superuser = True
        user.save(using=self._db)

        return user


class User(AbstractBaseUser, PermissionsMixin):
    username = models.CharField(max_length=30, unique=True)
    full_name = models.CharField(max_length=255)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    date_joined = models.DateTimeField(auto_now_add=True)

    objects = UserManager()

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = ['full_name']

    def __str__(self):
        return self.username

    def get_full_name(self):
        return self.full_name

    def get_short_name(self):
        return self.username


class Habit(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=30)
    startdate = models.CharField(max_length=50)
    nextdate = models.CharField(max_length=50)
    streak = models.IntegerField(default=0)
    stdtext = models.CharField(max_length=200)
    habitname = models.CharField(max_length=50, default=None)