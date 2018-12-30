My project was the creation and implementation of a 2-player
tic-tac-toe game between two devices communicating over Bluetooth.

Technical challenges faced:
-Communication over Bluetooth:
Unfortunately Android Studio's emulators can not use Bluetooth,
so I had to use two real Android phones to test the application.

-Buttons:
Making sure they organized themselves properly on the
screen. When they were disorganized, some buttons overlapped with
others, which prevented the user from pushing the buttons. I
solved this problem by using Horizontal and Vertical Guideline
views.

-Modifiability:
I wanted to design the program to be easy to edit and change
as needed. With 9 main buttons for the game part of the app,
it would be inconvenient to need to refer to each of these
buttons in code all the time, so I made an array that allows
me to access and iterate through each button easily.
