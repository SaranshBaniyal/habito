from django.urls import path
from . import views

urlpatterns = [
    path('signup/', views.signup, name='signup'),
    path('login/', views.login, name='login'),
    path('create/', views.create, name='create'),
    path('listall/', views.listall, name='listall'),

    # path('emosense/', views.emosense, name='emosense'),
]