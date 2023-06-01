from rest_framework import status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from .serializers import UserSerializer, HabitSerializer
from django.contrib.auth import authenticate
import datetime
from datetime import timedelta
import pytz
from rest_framework.permissions import AllowAny
from .models import Habit

import json
# import requests
import replicate

@api_view(['POST'])
@permission_classes([AllowAny])
def signup(request):
    serializer = UserSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([AllowAny])
def login(request):
    username = request.data.get('username')
    password = request.data.get('password')
    user = authenticate(request, username=username, password=password)
    if user is not None:
        return Response({'success': True, 'message': 'Login successful'})
    else:
        return Response({'success': False, 'message': 'Invalid credentials'}, status=status.HTTP_401_UNAUTHORIZED)



@api_view(['POST'])
@permission_classes([AllowAny])
def create(request):
    # request.data._mutable=True #just added to fix AttributeError: This QueryDict instance is immutable

    request.data['startdate'] = str(datetime.date.today())  # Returns 2018-01-15

    request.data['nextdate'] = get_next_date(str(datetime.date.today()))
    request.data['streak'] = 1

    imgurl = str(request.data.get('url'))
    output = replicate.run(
        "salesforce/blip:2e1dddc8621f72155f24cf2e0adbde548458d3cab9f00c0139eea840d0ac4746",
        input={"image": imgurl}
    )
    # print(output)
    request.data['stdtext'] = output


    serializer = HabitSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)










def get_date_difference(start_date, end_date):
    # Convert the date strings to datetime objects
    start_date = datetime.datetime.strptime(start_date, "%Y-%m-%d").astimezone(pytz.timezone('Asia/Kolkata'))
    end_date = datetime.datetime.strptime(end_date, "%Y-%m-%d").astimezone(pytz.timezone('Asia/Kolkata'))

    # Calculate the difference between the dates
    delta = end_date - start_date

    # Get the total number of days
    total_days = delta.days

    return total_days




def get_next_date(date_string):
    # Convert the date string to a datetime object
    date = datetime.datetime.strptime(date_string, "%Y-%m-%d")
    #.astimezone(pytz.timezone('Asia/Kolkata'))

    # Increment the date by one day
    next_date = date + timedelta(days=1)

    # Format the next date as a string
    next_date_string = next_date.strftime("%Y-%m-%d")
    #.astimezone(pytz.timezone('Asia/Kolkata'))

    return next_date_string
