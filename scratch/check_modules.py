import urllib.request
import urllib.error

modules = [
    'accessors',
    'base',
    'entity',
    'extensions',
    'networking',
    'obj-loader',
    'obj_loader',
    'tags',
    'transfer',
    'models',
    'client_events'
]
version = '2.3.16-beta.81+1.20.1'

headers = {'User-Agent': 'youforia-jp/ccc-ai-age/1.0.0'}

for m in modules:
    url = f'https://mvn.devos.one/snapshots/io/github/fabricators_of_create/Porting-Lib/{m}/{version}/'
    req = urllib.request.Request(url, headers=headers)
    try:
        urllib.request.urlopen(req)
        print(f"{m} exists")
    except urllib.error.HTTPError as e:
        print(f"{m}: {e.code}")
    except Exception as e:
        print(f"{m}: error {e}")
