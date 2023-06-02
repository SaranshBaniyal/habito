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
import requests
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

    data_copy = request.data.copy()
    data_copy['startdate'] = str(datetime.date.today())
    data_copy['nextdate'] = get_next_date(str(datetime.date.today()))
    data_copy['streak'] = 1

    imgurl = str(request.data.get('url'))
    output = replicate.run(
        "salesforce/blip:2e1dddc8621f72155f24cf2e0adbde548458d3cab9f00c0139eea840d0ac4746",
        input={"image": imgurl}
    )
    data_copy['stdtext'] = output
    # request.data['stdtext'] = output

    serializer = HabitSerializer(data=data_copy)
    if serializer.is_valid():
        serializer.save()
        return Response({'success': 1, 'message':'Habit Added'})
        # return Response(serializer.data, status=status.HTTP_201_CREATED)
    # return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    return Response({'success': 0, 'message': 'Unable to add this Habit'})


@api_view(['POST'])
@permission_classes([AllowAny])
def listall(request):
    username = request.data.get('username')
    queryset = Habit.objects.filter(username=username).order_by('startdate')
    data = list(queryset.values())
    return Response(data)




@api_view(['PATCH'])
@permission_classes([AllowAny])
def verify(request, pk):
    # request.data._mutable=True #just added to fix AttributeError: This QueryDict instance is immutable
    try:
        instance = Habit.objects.get(pk=pk)
    except Habit.DoesNotExist:
        return Response({'error': 'Object not found'}, status=404)
    
    if request.method == 'PATCH':
        if request.data.get('currentdate') == instance.nextdate:
            imgurl = str(request.data.get('url'))
            vtext = replicate.run("salesforce/blip:2e1dddc8621f72155f24cf2e0adbde548458d3cab9f00c0139eea840d0ac4746",
                                   input={"image": imgurl})
            
            API_TOKEN="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"    #huggingface api keys
            API_URL = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2"
            headers = {"Authorization": f"Bearer {API_TOKEN}"}
            payload = {
	"inputs": {
		"source_sentence": instance.stdtext,
		"sentences": [
			vtext, vtext]
    }
}
            data = json.dumps(payload)
            response = requests.request("POST", API_URL, headers=headers, data=data)
            score = float(list(response.json())[1])
            if score > 0.5:
                instance.streak = instance.streak + 1
                instance.nextdate = get_next_date(instance.nextdate)
                instance.save()
                return Response({'success': 1, 'message':'Streak Updated'}) #streak updated
            
            else:
                return Response({'success': 2, 'message':'Streak Verification Failed'}) #streak verification failed

        else:
            instance.startdate = str(datetime.date.today())
            instance.nextdate = get_next_date(str(datetime.date.today()))
            instance.streak=1;
            instance.save()
            return Response({'success': 0, 'message':'Streak Reset'}) #streak reset
    
    return Response({'error': 'Invalid request method'}, status=400)


@api_view(['DELETE'])
@permission_classes([AllowAny])
def delete(request, pk):
    try:
        instance = Habit.objects.get(pk=pk)
    except Habit.DoesNotExist:
        return Response({'error': 'Object not found'}, status=404)
    
    if request.method == 'DELETE':
        instance.delete()
        
        return Response({'success': 1})
    
    return Response({'error': 'Invalid request method'}, status=400)




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


@api_view(['GET'])
@permission_classes([AllowAny])
def test(request):
    API_TOKEN="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"    #huggingface api keys
    API_URL = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2"
    headers = {"Authorization": f"Bearer {API_TOKEN}"}
    payload = {
	"inputs": {
		"source_sentence": "ABCD",
		"sentences": [
			"vtext", "ABC"]
    }
}
    data = json.dumps(payload)
    response = requests.request("POST", API_URL, headers=headers, data=data)
    return Response(list(response.json())[1])