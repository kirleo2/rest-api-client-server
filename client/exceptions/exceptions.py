class EntityDoesNotExist(Exception):
    def __init__(self, msg):
        self.msg = msg


class UnexpectedServerError(Exception):
    def __init__(self, msg):
        self.msg = msg


class EntityAlreadyExists(Exception):
    def __init__(self, msg):
        self.msg = msg
