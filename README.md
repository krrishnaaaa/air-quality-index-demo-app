# Air Quality Index

A single activity application to display live Air Quality Monitoring data.

### Libraries Used
  1. [Socket.io](https://github.com/socketio/socket.io)
  2. [GreenRobot EventBus](https://github.com/greenrobot/EventBus)
  3. [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
  4. Android Room DB
  5. Other Android support and kotlin coroutine libraries

### Application Architecture 
This demo application uses MVVM architecture to get live data from web-socket server. The received data are stored in the Room Database and displays the changes on the fly.

The socket connection is opened as soon as application opens using [SocketManager](/app/src/main/java/com/pcsalt/example/airqualityindex/network/SocketManager.kt). The data received from socket is passed using EventBus to the repository. In repository the received data is stored, which is provided to the UI using LiveData.

#### Class Description
  1. *SocketManager*: To maintain socket connection
  2. *SocketListener*: To listen to the socket data and emit received data using EventBus
  3. *AQIRepo*: Repository to receive network data and to query/serve room db data
  4. *AQIDataViewModel*: ViewModel to provide data navigation
  5. *NumberExt*: Extension methods
  6. *DisplayAQIDataFragment*: To display the list of cities with AQI data
  7. *AQIDetailsFragment*: To display the chart with the data available in the db


*DisplayAQIDataFragment* fragment listens for the database changes and displays all available cities air quality monitoring data in a list.
The quality index changes is real time and the change in data band is notified using changing the background color of the index.

In case the band of any city is changed, the following method is used to take care of notifying the change by blinking the background of the quality index data. In this method, we are checking if the data is already available in the list or not. If the data is already present then just update that data using [notifyItemChanged(position, payload)](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#notifyItemChanged(int,%20java.lang.Object)) method of the [RecyclerView.Adapter](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter).
```
    fun setData(newData: List<AQIData>) {
        if (this.dataList.isNotEmpty() && newData.isNotEmpty()) {
            var newItemAdded = false
            for (data in newData) {
                val idx = this.dataList.indexOf(data)
                if (idx > -1) {
                    val old = this.dataList[idx]
                    this.dataList[idx] = data
                    notifyItemChanged(idx, DataPayload(old, data))
                } else {
                    this.dataList.add(data)
                    newItemAdded = true
                }
            }
            if (newItemAdded) {
                notifyDataSetChanged()
            }
        } else {
            this.dataList.addAll(newData)
            notifyDataSetChanged()
        }
    }
```

Clicking on any list item will navigate to *AQIDetailsFragment*. In this fragment [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) is used to display graph based on the historic data available in the room db.


Few [extension methods](/app/src/main/java/com/pcsalt/example/airqualityindex/ext/NumberExt.kt) are also created to reduce the data checks and formatting. 

#### Modification Required
  1. Graph presentation needs to be improved
  2. UI is basic and presentation can be improved

### Download APK
APK of the current state can be downloaded from [here](/app-debug.apk)
