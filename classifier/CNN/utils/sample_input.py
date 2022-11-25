import numpy as np
from sklearn.model_selection import train_test_split


def shuffle(X, Y):
    # This function shuffles two equal-length list/array, X and Y, together.
    # 假设len = 100，randomize就是一个0-99的列表
    randomize = np.arange(len(X))
    # 对randomize列表进行shuffle打乱，使0-99重排
    np.random.shuffle(randomize)
    # 返回X和Y，下标是shuffle打乱后的randomize列表
    return X[randomize], Y[randomize]


def LoadData():
    """
    载入数据
    """
    with open(r"C:\Users\罗钊\PycharmProjects\character_recognition\data\Init.csv") as f:
        lines = f.readlines()
    data_len = len(lines)

    x_all = np.zeros((data_len, 256), dtype=np.float32)
    y_all = np.zeros((data_len,), dtype=np.float32)
    for i, line in enumerate(lines):
        line_split = line.strip().split(",")
        x = np.array(line_split[:-1], dtype=np.float32)
        x_all[i, :] = x
        label = str(line_split[-1])
        if label == "TRUE":
            y_all[i] = 1
        elif label == "FALSE":
            y_all[i] = 0
    X_train, y_train = x_all, y_all
    # X_train, X_test, y_train, y_test = train_test_split(
    #     x_all, y_all, test_size=0.15, stratify=y_all
    # )

    with open(r"C:\Users\罗钊\PycharmProjects\character_recognition\data\Test.csv") as f:
        lines_test = f.readlines()
    data_len_test = len(lines_test)

    x_all_test = np.zeros((data_len_test, 256), dtype=np.float32)
    y_all_test = np.zeros((data_len_test,), dtype=np.float32)
    for i, line in enumerate(lines_test):
        line_split = line.strip().split(",")
        x = np.array(line_split[:-1], dtype=np.float32)
        x_all_test[i, :] = x
        label = str(line_split[-1])
        if label == "TRUE":
            y_all_test[i] = 1
        elif label == "FALSE":
            y_all_test[i] = 0
    X_test, y_test = x_all_test, y_all_test
    return X_train, X_test, y_train, y_test


if __name__ == "__main__":
    X_train, X_test, y_train, y_test = LoadData()
