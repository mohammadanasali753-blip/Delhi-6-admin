extends Node3D

var train: Node3D
var camera: Camera3D
var speed := 0.0
var throttle := false
var braking := false
var speed_label: Label
var horn_label: Label
const TRACK_LENGTH := 900.0

func _ready() -> void:
    _make_world()
    _make_track()
    _make_train()
    _make_camera()
    _make_ui()

func _mat(c: Color, rough := 0.8, metal := 0.0) -> StandardMaterial3D:
    var m := StandardMaterial3D.new()
    m.albedo_color = c
    m.roughness = rough
    m.metallic = metal
    return m

func _box(parent: Node3D, label: String, size: Vector3, pos: Vector3, color: Color, metal := 0.0) -> MeshInstance3D:
    var n := MeshInstance3D.new()
    n.name = label
    var mesh := BoxMesh.new()
    mesh.size = size
    n.mesh = mesh
    n.position = pos
    n.material_override = _mat(color, 0.75, metal)
    parent.add_child(n)
    return n

func _make_world() -> void:
    var env_node := WorldEnvironment.new()
    var env := Environment.new()
    env.background_mode = Environment.BG_SKY
    var sky := Sky.new()
    sky.sky_material = ProceduralSkyMaterial.new()
    env.sky = sky
    env.ambient_light_source = Environment.AMBIENT_SOURCE_COLOR
    env.ambient_light_color = Color(0.72, 0.78, 0.88)
    env.ambient_light_energy = 0.8
    env_node.environment = env
    add_child(env_node)
    var sun := DirectionalLight3D.new()
    sun.rotation_degrees = Vector3(-40, -25, 0)
    sun.light_energy = 1.1
    sun.shadow_enabled = true
    add_child(sun)
    _box(self, "Ground", Vector3(140, 0.5, TRACK_LENGTH + 200), Vector3(0, -0.55, -TRACK_LENGTH * 0.45), Color(0.25, 0.38, 0.19))

func _make_track() -> void:
    _box(self, "Ballast", Vector3(5.0, 0.2, TRACK_LENGTH), Vector3(0, -0.18, -TRACK_LENGTH * 0.45), Color(0.31, 0.30, 0.28))
    for x in [-0.72, 0.72]:
        _box(self, "SteelRail", Vector3(0.13, 0.16, TRACK_LENGTH), Vector3(x, -0.03, -TRACK_LENGTH * 0.45), Color(0.55, 0.59, 0.62), 0.7)
    var sleeper_mat := Color(0.28, 0.20, 0.15)
    for i in range(300):
        _box(self, "Sleeper_%03d" % i, Vector3(2.25, 0.12, 0.34), Vector3(0, -0.08, -i * 3.0), sleeper_mat)
    for side in [-1, 1]:
        _box(self, "Platform_%d" % side, Vector3(5.0, 0.28, 80.0), Vector3(side * 5.0, 0.05, -45.0), Color(0.55, 0.53, 0.47))
        for i in range(5):
            _box(self, "StationPillar_%d_%d" % [side, i], Vector3(0.22, 3.0, 0.22), Vector3(side * 5.0, 1.65, -20.0 - i * 12.0), Color(0.82, 0.78, 0.65))

func _make_train() -> void:
    train = Node3D.new()
    train.name = "Locomotive"
    add_child(train)
    _box(train, "EngineBody", Vector3(2.6, 2.5, 7.0), Vector3(0, 1.65, 0), Color(0.72, 0.12, 0.08))
    _box(train, "Cabin", Vector3(2.45, 1.25, 2.0), Vector3(0, 3.35, 1.55), Color(0.78, 0.16, 0.1))
    _box(train, "FrontWindow", Vector3(1.65, 0.58, 0.05), Vector3(0, 3.48, 2.58), Color(0.10, 0.20, 0.25), 0.25)
    _box(train, "YellowStripe", Vector3(2.62, 0.16, 6.7), Vector3(0, 2.0, 0), Color(0.95, 0.68, 0.08))
    for z in [-2.2, 2.2]:
        for x in [-1.0, 1.0]:
            var wheel := _box(train, "Wheel", Vector3(0.28, 0.8, 0.8), Vector3(x, 0.55, z), Color(0.08, 0.08, 0.08), 0.5)
            wheel.rotation.z = PI / 2
    for i in range(2):
        var coach := Node3D.new()
        coach.position.z = -7.0 - i * 8.0
        train.add_child(coach)
        _box(coach, "PassengerCoach", Vector3(2.5, 2.6, 7.3), Vector3(0, 1.6, 0), Color(0.12, 0.28, 0.68))
        _box(coach, "CoachRoof", Vector3(2.4, 0.2, 7.1), Vector3(0, 2.95, 0), Color(0.72, 0.73, 0.75))
        for z in [-2.4, -1.2, 0.0, 1.2, 2.4]:
            _box(coach, "Window", Vector3(0.04, 0.45, 0.7), Vector3(1.27, 2.0, z), Color(0.08, 0.16, 0.22))
            _box(coach, "Window", Vector3(0.04, 0.45, 0.7), Vector3(-1.27, 2.0, z), Color(0.08, 0.16, 0.22))

func _make_camera() -> void:
    camera = Camera3D.new()
    camera.position = Vector3(0, 6.0, 12.0)
    camera.rotation_degrees = Vector3(-18, 0, 0)
    camera.current = true
    train.add_child(camera)

func _make_ui() -> void:
    var layer := CanvasLayer.new()
    add_child(layer)
    var panel := PanelContainer.new()
    panel.position = Vector2(18, 18)
    panel.size = Vector2(260, 92)
    layer.add_child(panel)
    var labels := VBoxContainer.new()
    panel.add_child(labels)
    var title := Label.new()
    title.text = "INDIAN RAILWAY • OPEN WORLD"
    labels.add_child(title)
    speed_label = Label.new()
    speed_label.text = "Speed: 0 km/h"
    labels.add_child(speed_label)
    horn_label = Label.new()
    horn_label.text = "Ready to depart"
    labels.add_child(horn_label)
    var controls := HBoxContainer.new()
    controls.anchor_left = 0.0
    controls.anchor_top = 1.0
    controls.anchor_right = 1.0
    controls.anchor_bottom = 1.0
    controls.offset_top = -100
    controls.offset_left = 20
    controls.offset_right = -20
    controls.offset_bottom = -20
    layer.add_child(controls)
    var accel := Button.new()
    accel.text = "ACCELERATE"
    accel.size_flags_horizontal = Control.SIZE_EXPAND_FILL
    accel.button_down.connect(func(): throttle = true)
    accel.button_up.connect(func(): throttle = false)
    controls.add_child(accel)
    var brake := Button.new()
    brake.text = "BRAKE"
    brake.size_flags_horizontal = Control.SIZE_EXPAND_FILL
    brake.button_down.connect(func(): braking = true)
    brake.button_up.connect(func(): braking = false)
    controls.add_child(brake)
    var horn := Button.new()
    horn.text = "HORN"
    horn.size_flags_horizontal = Control.SIZE_EXPAND_FILL
    horn.pressed.connect(func(): horn_label.text = "Horn! (sound asset to be added)")
    controls.add_child(horn)

func _process(delta: float) -> void:
    if throttle:
        speed = minf(speed + 7.0 * delta, 110.0)
    elif braking:
        speed = maxf(speed - 22.0 * delta, 0.0)
    else:
        speed = maxf(speed - 0.6 * delta, 0.0)
    train.position.z -= speed * delta * 0.45
    if train.position.z < -TRACK_LENGTH + 80.0:
        train.position.z = 0.0
    speed_label.text = "Speed: %d km/h" % int(speed)
    camera.position = camera.position.lerp(Vector3(0, 6.0, 12.0), delta * 2.0)
