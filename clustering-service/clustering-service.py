import math

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
from scipy.spatial.distance import directed_hausdorff
from sklearn.cluster import DBSCAN
from paho.mqtt import client as mqtt_client

MQTT_CLIENT_ID = "Server_Side_MQTT_ID"
MQTT_SERVER = "yourmqttserver.com"
MQTT_SERVER_PORT = 1883

# Some visualization stuff, not so important
sns.set()
plt.rcParams['figure.figsize'] = (12, 12)

# Utility Functions
color_lst = plt.rcParams['axes.prop_cycle'].by_key()['color']
color_lst.extend(['firebrick', 'olive', 'indigo', 'khaki', 'teal', 'saddlebrown',
                  'skyblue', 'coral', 'darkorange', 'lime', 'darkorchid', 'dimgray'])


# computes hausdorff distance
def hausdorff(u, v):
    d = max(directed_hausdorff(u, v)[0], directed_hausdorff(v, u)[0])
    return d


# computes euclidean distance
def euclidean_distance(x, y):
    return math.sqrt(sum([(a - b) ** 2 for a, b in zip(x, y)]))


# convert from spatial to cartesian coordinates
def lat_lon_to_cartesian(lat, lon, R=6371.0072):
    lon_r = np.radians(lon)
    lat_r = np.radians(lat)

    x = R * np.cos(lat_r) * np.cos(lon_r)
    y = R * np.cos(lat_r) * np.sin(lon_r)
    z = R * np.sin(lat_r)

    return x, y, z


# plot trajectories (spatial coordinates)
def plot_trajectories(traj_lst):
    i = 1
    for traj in traj_lst:
        plt.plot(traj[:, 0], traj[:, 1], marker=i % 11)
        i = i + 1
    plt.rcParams['axes.unicode_minus'] = False
    plt.draw()
    plt.show()


# plot clustered trajectories
def plot_cluster(traj_lst, cluster_lst):
    '''
    Plots given trajectories with a color that is specific for every trajectory's own cluster index.
    Outlier trajectories which are specified with -1 in `cluster_lst` are plotted dashed with black color
    '''

    for traj, cluster in zip(traj_lst, cluster_lst):
        if cluster == -1:
            # Means it it a noisy trajectory, paint it black
            plt.plot(traj[:, 0], traj[:, 1], c='k', linestyle='dashed')
        else:
            plt.plot(traj[:, 0], traj[:, 1], c=color_lst[cluster % len(color_lst)])
    plt.rcParams['axes.unicode_minus'] = False
    plt.draw()
    plt.show()


# dictionary of trajectories by cluster
def trajs_by_cluster(traj_lst, cluster_lst):
    trajectories = {}
    counting_of_trajs = {}
    indexes_of_trajs = {}
    index = 0
    for traj, cluster in zip(traj_lst, cluster_lst):
        if cluster in trajectories:
            trajectories[cluster].append(traj)
            counting_of_trajs[cluster] = counting_of_trajs[cluster] + 1
            indexes_of_trajs[cluster].append(index)
        else:
            trajectories[cluster] = [traj]
            counting_of_trajs[cluster] = 1
            indexes_of_trajs[cluster] = [index]
        index = index + 1
    return trajectories, counting_of_trajs, indexes_of_trajs


# Connect to mqtt server
def connect_mqtt(server, port, clientid):
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)
    # Set Connecting Client ID
    client = mqtt_client.Client(clientid)
    # client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(server, port)
    return client


# Publish message in a mqtt connection (client contains the topic)
def publish(client, topic, msg):
    result = client.publish(topic, msg)
    status = result[0]
    if status == 0:
        print(f"Send `{msg}` to topic `{topic}`")
    else:
        print(f"Failed to send message to topic {topic}")


# notify users by text
def notify_users(user1, tracj1, user2, traj2, cluster, D):
    print('-> Messaging users', user1, 'and', user2, 'distance (m) =',D[user1][user2]*1000)


# notify users through mqtt
def notify_users_mqtt(client, user1, user2):
    publish(client, str(user1), str(user2))
    publish(client, str(user2), str(user1))


# main procedure
def main():
    # 1 - prepare dataset (it contains user id, latitude and longitude) emulating users tracks
    print('Reading dataset ...')
    data_folder = 'shortest-path'
    filename = '%s/tracks.txt' % data_folder
    df = pd.read_csv(filename, sep=' ', names=["ind", "lat", "lon"])
    df['res'] = df.set_index('ind').values.tolist()
    traj_data = df.groupby('ind')['res'].apply(list)

    traj_lst = []
    for data_instance in traj_data:
        traj_lst.append(np.asarray(data_instance))

    traj_lst_cartesian = []
    for data_instance in traj_data:
        temp = []
        for lan_lon in data_instance:
            temp.append(lat_lon_to_cartesian(lan_lon[0], lan_lon[1]))
        traj_lst_cartesian.append(np.asarray(temp))

    # Plotting
    print('Plotting dataset ...')
    plot_trajectories(traj_lst)

    # 2 - distance matrix
    print('Computing distance matrix ...')

    traj_count = len(traj_lst_cartesian)
    D = np.zeros((traj_count, traj_count))

    for i in range(traj_count):
        for j in range(i + 1, traj_count):
            distance = hausdorff(traj_lst_cartesian[i], traj_lst_cartesian[j])
            D[i, j] = distance
            D[j, i] = distance

    # 3 - clustering using dbscan
    print('Clustering ...')
    mdl = DBSCAN(eps=0.1, min_samples=1, metric='precomputed')
    cluster_lst = mdl.fit_predict(D)
    plot_cluster(traj_lst, cluster_lst)

    # get raw trajs associated to each cluster
    trajs_in_clusters, counting_trajs_by_cluster, indexes_trajs_by_cluster = trajs_by_cluster(traj_lst, cluster_lst)

    # print number of trajs associated to each cluster
    print('Number of trajectories associated to each cluster:')
    counting_trajs_by_cluster
    # plot histogram
    plt.bar(list(counting_trajs_by_cluster.keys()), counting_trajs_by_cluster.values(), color='g')
    plt.xlabel("Cluster")
    plt.ylabel("Number of paths")
    plt.draw()
    plt.show()

    # print indexes of trajs associated to each cluster
    print('Indexes of trajectories associated to each cluster:')
    indexes_trajs_by_cluster

    # Plot raw trajs in a given cluster
    plot_trajectories(trajs_in_clusters[40])
    plt.rcParams['axes.unicode_minus'] = False
    plt.xlabel("Latitude")
    plt.ylabel("Longitude")
    plt.legend(list(indexes_trajs_by_cluster[40]))
    plt.draw()
    plt.show()

    # creates MQTT connection
    client = connect_mqtt(MQTT_SERVER, MQTT_SERVER_PORT, MQTT_CLIENT_ID)

    # notify users about sharing similar tracjectories with others
    for cluster in indexes_trajs_by_cluster:
        if len(indexes_trajs_by_cluster[cluster]) > 1:
            print('Notifying users', indexes_trajs_by_cluster[cluster], 'in cluster', cluster)
            for user1 in range(0, len(indexes_trajs_by_cluster[cluster]) - 1):
                for user2 in range(user1+1, len(indexes_trajs_by_cluster[cluster])):
                    index_traj_user1 = indexes_trajs_by_cluster[cluster][user1]
                    traj_user1 = traj_lst[index_traj_user1]
                    index_traj_user2 = indexes_trajs_by_cluster[cluster][user2]
                    traj_user2 = traj_lst[index_traj_user2]
                    notify_users (index_traj_user1, traj_user1, index_traj_user2, traj_user2, cluster,D)
                    notify_users_mqtt (client, index_traj_user1, index_traj_user2)

    # close MQTT connection
    client.disconnect()


# run main procedure
main()
