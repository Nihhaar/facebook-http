# This project uses a separate http server for hosting images
# and its the same server that hosts the web app.
# Create needed data folders first by running the following commands:

mkdir ~/fbdata
mkdir ~/fbdata/posts

# Run the http server in the "fbdata" folder

cd ~/fbdata
python3 -m http.server 8000
