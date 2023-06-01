from django.urls import path
from . import views

urlpatterns = [
    path('signup/', views.signup, name='signup'),
    path('login/', views.login, name='login'),
    # path('input/', views.input, name='input'),
    # path('output/', views.output, name='output'),
    # path('outputall/', views.outputall, name='outputall'),

    # path('emosense/', views.emosense, name='emosense'),
]